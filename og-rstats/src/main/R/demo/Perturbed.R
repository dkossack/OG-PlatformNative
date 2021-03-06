##
 # Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 #
 # Please see distribution for license.
 ##

# Iterates a view over some historical market data that is perturbed slightly. This demonstrates
# injecting values into the engine.

Init ()

# Find a view identifier (omit view name for arbitrary view)
viewName <- "MultiCurrency Swap Portfolio View"
matchedViews <- Views (viewName)
if (length (matchedViews) == 0) {
  stop ("No view called '", viewName, "' defined")
} else {
  viewIdentifier <- matchedViews[1,1]
}

# Set up the view client descriptor to sample a historic period (three months ending 14 days ago). This
# will be used over each of our "shift" samples.
endTime <- Sys.time () - (14 * 86400)
startTime <- endTime - (90 * 86400)
viewClientDescriptor <- HistoricalMarketDataViewClient (viewIdentifier, startTime, endTime)

# Our shifts (-10%, -5%, 0, +5%, +10%) applied to some tickers
shiftAmounts <- c (0.90, 0.95, 1, 1.05, 1.10)
shiftTickers <- c ("OG_SYNTHETIC_TICKER~USDCASHP2M", "OG_SYNTHETIC_TICKER~USDCASHP3M", "OG_SYNTHETIC_TICKER~USDSWAPP5Y", "OG_SYNTHETIC_TICKER~USDSWAPP10Y",
                   "BLOOMBERG_TICKER~US0002M Index", "BLOOMBERG_TICKER~US0003M Index", "BLOOMBERG_TICKER~USSW5 Curncy", "BLOOMBERG_TICKER~USSW10 Curncy")

# Iterate over the shifts
results <- lapply (shiftAmounts, function (shift) {
  
  # Create the view client and configure. Note that we need to give the client's a unique name
  # or we will get the same one back each time as the descriptor matches.
  viewClient <- ViewClient (viewClientDescriptor, useSharedProcess = FALSE, clientName = toString (shift))
  ConfigureViewClient (viewClient, lapply (shiftTickers, function (ticker) { MarketDataOverride (value = shift, valueName = MarketDataRequirementNames.Market.Value, identifier = ticker, operation = "MULTIPLY") }))

  # Build the PV into this vector
  presentValue <- c ()

  # Run the view cycles
  TriggerViewCycle (viewClient)
  result <- GetViewResult (viewClient, -1)
  while (!is.null (result)) {

    # Trigger the next iteration while we work with the previous
    TriggerViewCycle (viewClient)

    # Extract the PV from our results
    print (paste ("Got cycle", viewCycleId.ViewComputationResultModel (result)))
    data <- results.ViewComputationResultModel (result)$Default
    print (paste (nrow (data), "row(s) of data"))
    portfolio <- data[which (data$type == "PORTFOLIO_NODE"),]
    columns.pv <- columns.ViewComputationResultModel (data, ValueRequirementNames.Present.Value)
    value.pv <- firstValue.ViewComputationResultModel (portfolio, columns.pv)
    print (paste ("PV for", valuationTime.ViewComputationResultModel (result), "with", shift, "=", value.pv))
    presentValue <- append (presentValue, if (is.numeric (value.pv)) value.pv else NA)

    # Get the next iteration
    print (paste ("Waiting for next cycle"))
    result <- GetViewResult (viewClient, -1, viewCycleId.ViewComputationResultModel (result))

  }

  # Release the view client and return the PV vector as the result of this iteration
  presentValue

})

# Build a data frame from the results
columns <- c ()
rows <- max (sapply (results, length))
for (i in seq (from = 1, to = length (shiftAmounts))) {
  columns <- append (paste ("\"x", shiftAmounts[i], "\" = results[[", i, "]]", sep = ""), columns)
  while (length (results[[i]]) < rows) {
    results[[i]] <- append (results[[i]], NA)
  }
}
columns <- paste (columns, collapse = ", ")
cmd <- paste ("data.frame (", columns, ")", sep = "")
result <- eval (parse (text = cmd))
