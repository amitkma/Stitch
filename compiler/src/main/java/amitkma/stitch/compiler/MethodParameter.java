package amitkma.stitch.compiler;

import com.squareup.javapoet.TypeName;

/**
 * Create by Amit Kumar on 19/5/17
 * Email : mr.doc10jl96@gmail.com
 * Company : Dot Wave Ltd.
 */

final class MethodParameter {

    static final MethodParameter[] NONE = new MethodParameter[0];

    private final int mListenerPosition;
    private final TypeName mTypeName;
    private final String mName;

    MethodParameter(int listenerPosition, TypeName typeName, String name) {
        this.mListenerPosition = listenerPosition;
        this.mTypeName = typeName;
        this.mName = name;
    }

    TypeName getTypeName() {
        return mTypeName;
    }

    public String getName() {
        return mName;
    }
}
