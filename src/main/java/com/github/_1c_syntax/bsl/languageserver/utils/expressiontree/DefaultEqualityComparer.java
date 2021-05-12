/*
 * This file is a part of BSL Language Server.
 *
 * Copyright © 2018-2021
 * Alexey Sosnoviy <labotamy@gmail.com>, Nikita Gryzlov <nixel2007@gmail.com> and contributors
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
package com.github._1c_syntax.bsl.languageserver.utils.expressiontree;

import com.github._1c_syntax.bsl.languageserver.utils.DiagnosticHelper;

import java.util.List;

public class DefaultEqualityComparer implements NodeEqualityComparer {

  @Override
  public boolean equal(BslExpression first, BslExpression second) {
    if (first == second)
      return true;

    if (first.getClass() != second.getClass() || first.getNodeType() != second.getNodeType())
      return false;

    switch (first.getNodeType()) {
      case LITERAL:
        return literalsEqual((TerminalSymbolNode) first, (TerminalSymbolNode) second);
      case IDENTIFIER:
        return identifiersEqual((TerminalSymbolNode) first, (TerminalSymbolNode) second);
      case BINARY_OP:
        return binaryOperationsEqual((BinaryOperationNode)first, (BinaryOperationNode)second);
      case UNARY_OP:
        return unaryOperationsEqual((UnaryOperationNode)first, (UnaryOperationNode)second);
      case TERNARY_OP:
        return ternaryOperatorsEqual((TernaryOperatorNode)first, (TernaryOperatorNode)second);
      case SKIPPED_CALL_ARG:
        return true;
      case CALL:
        return callStatementsEqual((AbstractCallNode)first, (AbstractCallNode)second);
      default:
        throw new IllegalStateException();
    }

  }

  private boolean callStatementsEqual(AbstractCallNode first, AbstractCallNode second) {
    if(first instanceof MethodCallNode){
      return methodCallsEqual((MethodCallNode)first, (MethodCallNode)second);
    }
    else {
      return constructorCallsEqual((ConstructorCallNode)first, (ConstructorCallNode)second);
    }
  }

  private boolean constructorCallsEqual(ConstructorCallNode first, ConstructorCallNode second) {
    return equal(first.getTypeName(), second.getTypeName()) && argumentsEqual(first.arguments(), second.arguments());
  }

  private boolean argumentsEqual(List<BslExpression> argumentsOfFirst, List<BslExpression> argumentsOfSecond) {

    if(argumentsOfFirst.size() != argumentsOfSecond.size())
      return false;

    for (int i = 0; i < argumentsOfFirst.size(); i++){
      if(!equal(argumentsOfFirst.get(i), argumentsOfSecond.get(i))){
        return false;
      }
    }

    return true;
  }

  private boolean methodCallsEqual(MethodCallNode first, MethodCallNode second) {
    return first.getName().getText().equalsIgnoreCase(second.getName().getText())
      && argumentsEqual(first.arguments(), second.arguments());
  }

  private boolean ternaryOperatorsEqual(TernaryOperatorNode first, TernaryOperatorNode second) {
    return equal(first.getCondition(), second.getCondition()) &&
      equal(first.getTruePart(), second.getTruePart()) &&
      equal(first.getFalsePart(), second.getFalsePart());
  }

  private boolean unaryOperationsEqual(UnaryOperationNode first, UnaryOperationNode second) {
    if(first.getOperator() != second.getOperator())
      return false;

    return equal(first.getOperand(), second.getOperand());
  }

  private boolean binaryOperationsEqual(BinaryOperationNode first, BinaryOperationNode second) {
    if(first.getOperator() != second.getOperator())
      return false;

    return equal(first.getLeft(), second.getLeft()) && equal(first.getRight(), second.getRight());

  }

  private boolean identifiersEqual(TerminalSymbolNode first, TerminalSymbolNode second) {
    return DiagnosticHelper.equalNodes(first.getRepresentingAst(), second.getRepresentingAst());
  }

  private boolean literalsEqual(TerminalSymbolNode first, TerminalSymbolNode second) {
    return DiagnosticHelper.equalNodes(first.getRepresentingAst(), second.getRepresentingAst());
  }
}
