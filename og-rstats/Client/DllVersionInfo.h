/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */

#ifndef __inc_og_rstats_client_dllversioninfo_h
#define __inc_og_rstats_client_dllversioninfo_h

#define DllVersion_FileDescription	"OpenGamma client library for R"

#ifdef _WIN32
#define DllVersion_OriginalFilename	"OpenGamma.dll"
#else /* ifdef _WIN32 */
#define DllVersion_OriginalFilename	"OpenGamma.so"
#endif /* ifdef _WIN32 */

#define DllVersion_ProductName		"OpenGamma/R"

#endif /* ifndef __inc_og_rstats_client_dllversioninfo_h */
