package net.thenextlvl.service.converter;

import net.thenextlvl.service.api.Controller;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
@FunctionalInterface
public interface Converter<C extends Controller> {
    CompletableFuture<Void> convert(C source, C target);
}
