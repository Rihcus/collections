import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

public class HiveQueryLiteralsRemover {
    public static String removeLiterals(String query) {
        // Parse the query into an AST
        ASTNode ast = parseQuery(query);

        // Traverse the AST to remove all literals
        removeLiterals(ast);

        // Convert the modified AST back into a query string
        return ast.toStringTree().replaceAll("\\\\'", "'");
    }

    private static ASTNode parseQuery(String query) {
        // Create an ANTLR input stream from the query string
        CharStream input = CharStreams.fromString(query);

        // Create a lexer and token stream for the input
        HiveLexer lexer = new HiveLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Create a parser and parse the tokens into an AST
        HiveParser parser = new HiveParser(tokens);
        return parser.statement().getTree();
    }

    private static void removeLiterals(ASTNode node) {
        // If the node is a literal, remove it from the parent
        if (node.getToken() != null && node.getToken().getType() == HiveParser.StringLiteral) {
            ASTNode parent = (ASTNode) node.getParent();
            int index = parent.getIndex(node);
            parent.deleteChild(index);
        } else {
            // Otherwise, recursively traverse the node's children
            for (int i = 0; i < node.getChildCount(); i++) {
                ASTNode child = (ASTNode) node.getChild(i);
                removeLiterals(child);
            }
        }
    }
}
