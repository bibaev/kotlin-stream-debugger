// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.debugger.streams.kotlin.lib

import com.intellij.debugger.streams.wrapper.StreamChain
import com.intellij.debugger.streams.wrapper.StreamChainBuilder
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement

// The Sequence Debugger was integrated into the Kotlin plugin 1.3M2.
// This object contains utility functions to avoid compatibility issues with new versions of the kotlin plugin
// The separate Sequence Debugger plugin will be removed after the Kotlin plugin 1.3 release
object CompatibilityUtil {
    fun makeCompatible(chainBuilder: () -> StreamChainBuilder): StreamChainBuilder {
        return if (!ApplicationManager.getApplication().isUnitTestMode && KotlinVersion.CURRENT.isAtLeast(1, 3)) {
            NoChainBuilder
        } else {
            chainBuilder.invoke()
        }
    }

    private object NoChainBuilder : StreamChainBuilder {
        val LOG = Logger.getInstance(NoChainBuilder::class.java)

        override fun isChainExists(startElement: PsiElement): Boolean {
            LOG.info("Chain builder from sequence debugger plugin disabled")
            return false
        }

        override fun build(startElement: PsiElement): List<StreamChain> {
            LOG.error("Unexpected call. This method should not be called")
            return emptyList()
        }

    }
}