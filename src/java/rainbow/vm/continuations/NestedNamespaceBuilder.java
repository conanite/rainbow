package rainbow.vm.continuations;

import rainbow.types.ArcObject;

public class NestedNamespaceBuilder extends ContinuationSupport {

  public NestedNamespaceBuilder(NamespaceBuilder namespaceBuilder) {
    super(null, null, namespaceBuilder);
  }

  public void digest(ArcObject o) {
    ((NamespaceBuilder) whatToDo).start();
  }
}
