/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */

#include "stdafx.h"
#include "Entities.h"
#include "globals.h"
#include "Errors.h"
#include "Parameters.h"

LOGGING (com.opengamma.rstats.package.Entities);

/// Returns the indexed entry.
///
/// @param[in] index zero-based index of the entry
/// @return the entry, or NULL if an error was issued (e.g. the index is invalid)
const CEntityEntry *REntities::GetEntry (SEXP index) const {
	if (m_poEntities) {
		if (isInteger (index)) {
			const CEntityEntry *poEntry = GetEntryImpl (*INTEGER (index));
			if (poEntry != NULL) {
				return poEntry;
			} else {
				LOGERROR (ERR_PARAMETER_VALUE);
				return NULL;
			}
		} else {
			LOGERROR (ERR_PARAMETER_TYPE);
			return NULL;
		}
	} else {
		LOGERROR (ERR_INITIALISATION);
		return NULL;
	}
}

SEXP REntities::Count () const {
	if (m_poEntities) {
		SEXP count = allocVector (INTSXP, 1);
		*INTEGER (count) = m_poEntities->Size ();
		return count;
	} else {
		LOGERROR (ERR_INITIALISATION);
		return R_NilValue;
	}
}

SEXP REntities::GetName (SEXP index) const {
	REntityEntry oE (GetEntry (index));
	return oE.GetName ();
}

SEXP REntities::GetParameterFlags (SEXP index) const {
	REntityEntry oE (GetEntry (index));
	return oE.GetParameterFlags ();
}

SEXP REntities::GetParameterNames (SEXP index) const {
	REntityEntry oE (GetEntry (index));
	return oE.GetParameterNames ();
}

SEXP REntityEntry::GetName () const {
	if (m_poEntry) {
		return mkString (m_poEntry->GetName ());
	} else {
		return R_NilValue;
	}
}

SEXP REntityEntry::GetParameterFlags () const {
	if (m_poEntry) {
		SEXP flags = allocVector (INTSXP, m_poEntry->GetParameterCount ());
		int n;
		for (n = 0; n < m_poEntry->GetParameterCount (); n++) {
			INTEGER (flags)[n] = m_poEntry->GetParameter (n)->GetFlags ();
		}
		return flags;
	} else {
		return R_NilValue;
	}
}

SEXP REntityEntry::GetParameterNames () const {
	if (m_poEntry) {
		SEXP names = allocVector (STRSXP, m_poEntry->GetParameterCount ());
		PROTECT (names);
		int n;
		for (n = 0; n < m_poEntry->GetParameterCount (); n++) {
			SEXP name = mkChar (m_poEntry->GetParameter (n)->GetName ());
			PROTECT (name);
			SET_STRING_ELT (names, n, name);
		}
		UNPROTECT (1 + n);
		return names;
	} else {
		return R_NilValue;
	}
}