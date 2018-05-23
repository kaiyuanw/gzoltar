package com.gzoltar.core.model;

import com.gzoltar.core.instr.granularity.GranularityLevel;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.bytecode.Descriptor;

public final class NodeFactory {

  /**
   * Create a {@link com.gzoltar.core.model.Node} object and its ancestors, if not found.
   * 
   * @param ctClass
   * @param ctBehavior
   * @param lineNumber
   * @return An object of {@link com.gzoltar.core.model.Node}
   */
  public static Node createNode(final GranularityLevel granularity, final CtClass ctClass,
      final CtBehavior ctBehavior, final int lineNumber) {

    String packageName = ctClass.getPackageName() == null ? "" : ctClass.getPackageName();

    StringBuilder className = new StringBuilder(packageName);
    className.append(NodeType.CLASS.getSymbol());
    className.append(ctClass.getSimpleName());

    StringBuilder methodName = className;
    methodName.append(NodeType.METHOD.getSymbol());
    methodName.append(ctBehavior.getName());
    methodName.append(Descriptor.toString(ctBehavior.getSignature()));

    if (granularity.equals(GranularityLevel.METHOD)) {
      return new Node(methodName.toString(), lineNumber, NodeType.METHOD);
    }

    assert granularity.equals(GranularityLevel.BASICBLOCK)
        || granularity.equals(GranularityLevel.LINE);

    StringBuilder lineName = methodName;
    lineName.append(NodeType.LINE.getSymbol());
    lineName.append(String.valueOf(lineNumber));

    return new Node(lineName.toString(), lineNumber, NodeType.LINE);
  }

  /**
   * Parse a {@link com.gzoltar.core.model.Node} object and create its ancestors.
   * @param tree
   * @param node
   */
  public static void createNode(final Tree tree, final Node node) {

    final String nodeName = node.getName();
    final NodeType nodeType = node.getNodeType();

    final int lineNumber = Integer.valueOf(
        nodeName.substring(nodeName.indexOf(NodeType.LINE.getSymbol()) + 1, nodeName.length()));

    // === Package ===

    if (nodeType.equals(NodeType.PACKAGE)) {
      node.setParent(tree.getRoot());
      tree.addNode(node);
      return;
    }

    final String packageName = nodeName.substring(0,
        nodeType.equals(NodeType.PACKAGE) ? nodeName.indexOf(NodeType.LINE.getSymbol())
            : nodeName.indexOf(NodeType.CLASS.getSymbol()));
    Node packageNode = tree.getNode(packageName);
    if (packageNode == null) {
      packageNode = new Node(packageName, lineNumber, NodeType.PACKAGE, tree.getRoot());
      tree.addNode(packageNode);
    }

    // === Class ===

    if (nodeType.equals(NodeType.CLASS)) {
      node.setParent(packageNode);
      tree.addNode(node);
      return;
    }

    final String className = nodeName.substring(0,
        nodeType.equals(NodeType.CLASS) ? nodeName.indexOf(NodeType.LINE.getSymbol())
            : nodeName.indexOf(NodeType.METHOD.getSymbol()));
    Node classNode = tree.getNode(className);
    if (classNode == null) {
      classNode = new Node(className, lineNumber, NodeType.CLASS, packageNode);
      tree.addNode(classNode);
    }

    // === Method ===

    if (nodeType.equals(NodeType.METHOD)) {
      node.setParent(classNode);
      tree.addNode(node);
      return;
    }

    final String methodName = nodeName.substring(0, nodeName.indexOf(NodeType.LINE.getSymbol()));
    Node methodNode = tree.getNode(methodName);
    if (methodNode == null) {
      methodNode = new Node(methodName, lineNumber, NodeType.METHOD, classNode);
      tree.addNode(methodNode);
    }

    // === Line ===

    node.setParent(methodNode);
    tree.addNode(node);
  }

}
