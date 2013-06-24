/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */

#ifndef STDAFX_H
#define STDAFX_H

#ifdef _WIN32
#define WIN32_LEAN_AND_MEAN
#define NOGDI
#include <Windows.h>
#include <wchar.h>
#ifdef __cplusplus
#pragma warning(disable:4995)
#endif /* ifdef __cplusplus */
#else /* ifdef _WIN32 */
#include <stdio.h>
#include <stdlib.h>
#endif /* ifdef _WIN32 */

#include <assert.h>

#include <Util/Fudge.h>
#ifdef __cplusplus
#include <Connector/Connector.h>
#include <Util/Logging.h>
#include <Util/String.h>
#ifdef _WIN32
#include <hash_map>
#else /* ifdef _WIN32 */
#include <tr1/unordered_map>
#endif /* ifdef _WIN32 */
#endif /* ifdef __cplusplus */

#ifndef Client
#define Client(path) #path
#endif /* ifndef Client */

#endif /* ifndef STDAFX_H */
