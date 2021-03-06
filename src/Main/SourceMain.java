package Main;

import AssiciationRule.AprioriFrequentItemsetGenerator;
import AssiciationRule.AssociationRule;
import AssiciationRule.AssociationRuleGenerator;
import AssiciationRule.FrequentItemsetData;

import java.sql.*;
import java.util.*;

/**
 * Created by Dark Wolf on 03/04/2017.
 */
public class SourceMain {
    public static void main(String[] args) {
        demo();
    }

    private static void demo() {
        double minSup = 0.02, minConf = 0.4; // minimum Support and Minimum Confidence
        AprioriFrequentItemsetGenerator<String> generator = new AprioriFrequentItemsetGenerator<>();

        // Input from mysql: test, user=root, pass="",table data_for_association_rule
        List<Set<String>> itemsetList = new ArrayList<>(); // New Array list
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM data_for_association_rule");
            while (rs.next()) {
                String[] tempArrayListStr = rs.getString(2).split(","); // split by ,
                itemsetList.add(new HashSet<>(Arrays.asList(tempArrayListStr)));
            }
        } catch (SQLException | IllegalAccessException | ClassNotFoundException | InstantiationException e) {
            e.printStackTrace();
            System.out.println("Have some problem in input");
            return;
        }

//        List<Set<String>> itemsetList = new ArrayList<>();
//
//        itemsetList.add(new HashSet<>(Arrays.asList("a", "b")));
//        itemsetList.add(new HashSet<>(Arrays.asList("b", "c", "d")));
//        itemsetList.add(new HashSet<>(Arrays.asList("a", "c", "d", "e")));
//        itemsetList.add(new HashSet<>(Arrays.asList("a", "d", "e")));
//        itemsetList.add(new HashSet<>(Arrays.asList("a", "b", "c")));
//
//        itemsetList.add(new HashSet<>(Arrays.asList("a", "b", "c", "d")));
//        itemsetList.add(new HashSet<>(Arrays.asList("a")));
//        itemsetList.add(new HashSet<>(Arrays.asList("a", "b", "c")));
//        itemsetList.add(new HashSet<>(Arrays.asList("a", "b", "d")));
//        itemsetList.add(new HashSet<>(Arrays.asList("b", "c", "e")));
//
        long startTime = System.nanoTime();
        FrequentItemsetData<String> data = generator.generate(itemsetList, minSup);
        long endTime = System.nanoTime();

        int i = 1;

        System.out.println("--- Frequent itemsets ---");

        for (Set<String> itemset : data.getFrequentItemsetList()) {
            System.out.printf("%2d: %9s, support: %1.1f\n",
                    i++,
                    itemset,
                    data.getSupport(itemset));
        }

        System.out.printf("Mined frequent itemset in %d milliseconds.\n",
                (endTime - startTime) / 1_000_000);

        startTime = System.nanoTime();
        List<AssociationRule<String>> associationRuleList =
                new AssociationRuleGenerator<String>()
                        .mineAssociationRules(data, minConf);
        endTime = System.nanoTime();

        i = 1;

        System.out.println();
        System.out.println("--- Association rules ---");

        for (AssociationRule<String> rule : associationRuleList) {
            System.out.printf("%2d: %s\n", i++, rule);
        }

        System.out.printf("Mined association rules in %d milliseconds.\n",
                (endTime - startTime) / 1_000_000);
    }
}
