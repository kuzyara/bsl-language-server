/*
 * This file is a part of BSL Language Server.
 *
 * Copyright (c) 2018-2023
 * Alexey Sosnoviy <labotamy@gmail.com>, Nikita Fedkin <nixel2007@gmail.com> and contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * BSL Language Server is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * BSL Language Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with BSL Language Server.
 */
package com.github._1c_syntax.bsl.languageserver.commands;

import com.github._1c_syntax.bsl.languageserver.codelenses.AbstractMethodComplexityCodeLensSupplier;
import com.github._1c_syntax.bsl.languageserver.inlayhints.AbstractComplexityInlayHintSupplier;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class AbstractToggleComplexityInlayHintsCommandSupplier
  implements CommandSupplier<AbstractToggleComplexityInlayHintsCommandSupplier.ToggleComplexityInlayHintsCommandArguments>
{
  private final AbstractComplexityInlayHintSupplier complexityInlayHintSupplier;

  @Override
  public Class<ToggleComplexityInlayHintsCommandArguments> getCommandArgumentsClass() {
    return ToggleComplexityInlayHintsCommandArguments.class;
  }

  @Override
  public Optional<Object> execute(ToggleComplexityInlayHintsCommandArguments arguments) {
    complexityInlayHintSupplier.toggleHints(arguments.getUri(), arguments.getMethodName());
    return Optional.empty();
  }

  @Override
  public boolean needRefreshInlayHintsAfterExecuteCommand() {
    return true;
  }

  @Value
  @EqualsAndHashCode(callSuper = true)
  @ToString(callSuper = true)
  public static class ToggleComplexityInlayHintsCommandArguments extends DefaultCommandArguments {
    /**
     * Имя метода.
     */
    String methodName;

    @ConstructorProperties({"uri", "id", "methodName"})
    public ToggleComplexityInlayHintsCommandArguments(URI uri, String id, String methodName) {
      super(uri, id);
      this.methodName = methodName;
    }

    public ToggleComplexityInlayHintsCommandArguments(String id, AbstractMethodComplexityCodeLensSupplier.ComplexityCodeLensData data) {
      this(data.getUri(), id, data.getMethodName());
    }
  }
}
