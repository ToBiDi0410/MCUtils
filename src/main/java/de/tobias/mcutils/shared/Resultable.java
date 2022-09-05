package de.tobias.mcutils.shared;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Resultable {

    ArrayList<Consumer<Resultable>> resultListeners = new ArrayList<>();
    ArrayList<Consumer<Resultable>> errorListeners = new ArrayList<>();

    public Resultable() {}

    public void onResult(Consumer<Resultable> cons) {
        resultListeners.add(cons);
    }

    public void onError(Consumer<Resultable> cons) {
        errorListeners.add(cons);
    }

    public void error() {
        for(Consumer<Resultable> cons : errorListeners) {
            cons.accept(this);
        }
    }

    public void result() {
        for(Consumer<Resultable> cons : resultListeners) {
            cons.accept(this);
        }
    }
}
