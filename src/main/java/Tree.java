import java.util.List;

public class Tree {

    final String kind;
    final List<Tree> children;
    final String str_value;
    final Integer num_value;

    Tree(String ss) {
        kind = "AString";
        children = null;
        str_value = ss;
        num_value = null;
    }

    Tree(Integer zz) {
        kind = "ANumber";
        children = null;
        str_value = null;
        num_value = zz;
    }

    Tree(List<Tree> _data) {
        kind = "ABranch";
        children = _data;
        str_value = null;
        num_value = null;
    }



}
