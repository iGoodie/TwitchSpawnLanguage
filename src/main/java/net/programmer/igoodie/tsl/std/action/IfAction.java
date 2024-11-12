package net.programmer.igoodie.tsl.std.action;

import net.programmer.igoodie.tsl.TSLPlatform;
import net.programmer.igoodie.tsl.exception.TSLPerformingException;
import net.programmer.igoodie.tsl.exception.TSLSyntaxException;
import net.programmer.igoodie.tsl.parser.TSLParser;
import net.programmer.igoodie.tsl.runtime.action.TSLAction;
import net.programmer.igoodie.tsl.runtime.event.TSLEventContext;
import net.programmer.igoodie.tsl.runtime.predicate.TSLPredicate;
import net.programmer.igoodie.tsl.util.Pair;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

// IF <C> THEN <A> [ELSE <A>]
public class IfAction extends TSLAction {

    protected TSLPredicate condition;
    protected TSLAction thenAction;
    protected TSLAction elseAction;

    public IfAction(TSLPlatform platform, List<String> args) throws TSLSyntaxException {
        super(platform, args);

        splitIfElse(args).using((ifPart, elsePart) -> {
            splitCondition(ifPart).using((conditionArgs, bodyArgs) -> {
                this.condition = new TSLParser(platform, conditionArgs).parsePredicate();
                this.thenAction = new TSLParser(platform, bodyArgs).parseAction();
            });

            if (elsePart.size() > 0) {
                this.elseAction = new TSLParser(platform, elsePart).parseAction();
            }
        });
    }

    protected Pair<List<String>, List<String>> splitIfElse(List<String> args) throws TSLSyntaxException {
        int indexElse = IntStream.range(0, args.size())
                .filter(i -> args.get(i).equalsIgnoreCase("ELSE"))
                .findFirst().orElse(-1);

        if (indexElse == -1) return new Pair<>(args, Collections.emptyList());

        List<String> elseActionChunk = args.subList(indexElse + 1, args.size());

        if (elseActionChunk.size() == 0) {
            throw new TSLSyntaxException("Excepted an action after 'ELSE'.");
        }

        return new Pair<>(args.subList(0, indexElse), elseActionChunk);
    }

    protected Pair<List<String>, List<String>> splitCondition(List<String> args) throws TSLSyntaxException {
        int indexThen = IntStream.range(0, args.size())
                .filter(i -> args.get(i).equalsIgnoreCase("THEN"))
                .findFirst().orElse(-1);

        if (indexThen == -1) {
            throw new TSLSyntaxException("Expected 'THEN' after condition.");
        }

        return new Pair<>(args.subList(0, indexThen), args.subList(indexThen + 1, args.size()));
    }

    @Override
    public boolean perform(TSLEventContext ctx) throws TSLPerformingException {
        if (ctx.getPerformingRule() == null) {
            throw new TSLPerformingException("Cannot perform IF action, outside a TSL Rule.");
        }

        if (condition.test(ctx.getPerformingRule(), ctx)) {
            return thenAction.perform(ctx);

        } else if (elseAction != null) {
            return elseAction.perform(ctx);
        }

        return true;
    }

//    public static void main(String[] args) throws TSLSyntaxException, TSLPerformingException, IOException {
//        TSLPlatform platform = new TSLPlatform("", 1.0f);
//
//        platform.initializeStd();
//
//        platform.registerAction("DEBUG", (platform1, args1) -> new TSLAction(platform, args1) {
//            @Override
//            public boolean perform(TSLEventContext ctx) throws TSLPerformingException {
//                System.out.println(args1);
//                return true;
//            }
//        });
//
//        platform.registerEvent(new TSLEvent("Donation")
//                .addPropertyType(TSLEvent.PropertyBuilder.DOUBLE.create("amount")));
//
//        String script = String.join("\n",
//                "",
//                "IF amount IN RANGE [1,2] THEN",
//                " DEBUG Hey",
//                "ELSE IF amount = 3 THEN",
//                " DEBUG %OH NO!%",
//                "ON Donation"
//        );
//
//        TSLLexer lexer = new TSLLexer(CharStream.fromString(script));
//        TSLParser parser = new TSLParser(platform, "Player:iGoodie", lexer.tokenize());
//        TSLRuleset ruleset = parser.parse();
//
//        TSLEventContext ctx = new TSLEventContext(platform, "Donation");
//        ctx.getEventArgs().put("amount", 4f);
//
//        ruleset.perform(ctx);
//    }

}
