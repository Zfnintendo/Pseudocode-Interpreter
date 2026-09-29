package ast;

import java.util.List;

public record ArrayType(List<ArrayBounds> bounds, TypeSpecification elementType) implements TypeSpecification {

    public ArrayType {
        bounds = List.copyOf(bounds);
    }

}
