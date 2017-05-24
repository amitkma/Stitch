package amitkma.stitch.compiler;

import com.squareup.javapoet.TypeName;

import java.util.List;

/**
 * Create by Amit Kumar on 19/5/17
 * Email : mr.doc10jl96@gmail.com
 * Company : Dot Wave Ltd.
 */

public class MethodThreadStitching {

    private final String mName;
    private final TypeName mReturnType;
    private final List<MethodParameter> mMethodParameterList;
    private final int mThreadType;


    public MethodThreadStitching(String name, TypeName returnType, List parameterList,
            int threadType) {
        this.mName = name;
        this.mReturnType = returnType;
        this.mMethodParameterList = parameterList;
        this.mThreadType = threadType;
    }

    public String getName() {
        return mName;
    }

    public List<MethodParameter> getMethodParameterList() {
        return mMethodParameterList;
    }

    public int getThreadType() {
        return mThreadType;
    }

    public TypeName getReturnType() {
        return mReturnType;
    }
}
