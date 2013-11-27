
package org.n52.sir.xml;

import java.util.HashSet;
import java.util.Set;

import org.n52.sir.xml.ITransformer.TransformableFormat;
import org.n52.sir.xml.impl.SMLtoEbRIMTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class TransformerModule extends AbstractModule {

    private static Logger log = LoggerFactory.getLogger(TransformerModule.class);

    @Override
    protected void configure() {
        Multibinder<ITransformer> listenerBinder = Multibinder.newSetBinder(binder(), ITransformer.class);
        listenerBinder.addBinding().to(SMLtoEbRIMTransformer.class);

        log.debug("configured {}", this);
    }

    public static ITransformer getFirstMatchFor(Set<ITransformer> transformers,
                                                TransformableFormat input,
                                                TransformableFormat output) {
        Set<ITransformer> set = getForOutput(getForInput(transformers, input), output);
        if ( !set.isEmpty())
            return set.iterator().next();

        return null;
    }

    public static Set<ITransformer> getFor(Set<ITransformer> transformers,
                                           TransformableFormat input,
                                           TransformableFormat output) {
        return getForOutput(getForInput(transformers, input), output);
    }

    private static Set<ITransformer> getForOutput(Set<ITransformer> transformers, TransformableFormat output) {
        Set<ITransformer> filtered = new HashSet<>();
        for (ITransformer t : transformers) {
            if (t.producesOutput(output))
                filtered.add(t);
        }
        return filtered;
    }

    private static Set<ITransformer> getForInput(Set<ITransformer> transformers, TransformableFormat input) {
        Set<ITransformer> filtered = new HashSet<>();
        for (ITransformer t : transformers) {
            if (t.acceptsInput(input))
                filtered.add(t);
        }
        return filtered;
    }

}
