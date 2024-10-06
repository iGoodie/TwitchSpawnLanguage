package net.programmer.igoodie;

import net.programmer.igoodie.runtime.action.TSLAction;
import net.programmer.igoodie.runtime.event.TSLEvent;
import net.programmer.igoodie.runtime.predicate.comparator.TSLComparator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class TSL {

    private final String platformName;

    private final Map<String, TSLAction.Supplier<?>> actionDefinitions;
    private final Map<String, TSLAction.ExpressionEvaluator> expressionEvaluators;
    private final Map<String, TSLEvent> eventDefinitions;
    private final Map<String, TSLComparator.Supplier<?>> comparatorDefinitions;

    public TSL(String platformName) {
        this.platformName = platformName;
        this.actionDefinitions = new HashMap<>();
        this.expressionEvaluators = new HashMap<>();
        this.eventDefinitions = new HashMap<>();
        this.comparatorDefinitions = new HashMap<>();
    }

    public <T extends TSLAction.Supplier<?>> T registerAction(String name, T action) {
        this.actionDefinitions.put(name.toUpperCase(), action);
        return action;
    }

    public <T extends TSLAction.ExpressionEvaluator> T registerExpression(String expression, T evaluator) {
        this.expressionEvaluators.put(expression, evaluator);
        return evaluator;
    }

    public <T extends TSLEvent> T registerEvent(T event) {
        this.eventDefinitions.put(event.getEventName(), event);
        return event;
    }

    public <T extends TSLComparator.Supplier<?>> T registerComparator(String symbol, T comparator) {
        this.comparatorDefinitions.put(symbol.toUpperCase(), comparator);
        return comparator;
    }

    public Optional<TSLAction.Supplier<?>> getActionDefinition(String name) {
        return Optional.ofNullable(this.actionDefinitions.get(name));
    }

    public Optional<TSLAction.ExpressionEvaluator> getExpressionEvaluator(String expression){
        return Optional.ofNullable(expressionEvaluators.get(expression));
    }

    public Optional<TSLEvent> getEvent(String eventName) {
        return Optional.ofNullable(this.eventDefinitions.get(eventName));
    }

    public Optional<TSLComparator.Supplier<?>> getComparatorDefinition(String symbol) {
        return Optional.ofNullable(this.comparatorDefinitions.get(symbol));
    }

}
