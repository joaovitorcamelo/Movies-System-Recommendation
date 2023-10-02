import com.opencsv.CSVWriter;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Recommender {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double userId = 0; // Get the next available user ID from ratings_small.csv

        try {
            Table ratings = Table.read().file("./ratings_small.csv");
            NumberColumn firstColumn = (NumberColumn) ratings.column(0);
            userId = firstColumn.max();
        }
        catch (Exception e){
            System.out.print(e);
        }

        userId++;

        for (int i = 0; i < 10; i++) {
            try {
                // Display 4 random movies
                Table movies = Table.read().file("./movies_metadata.csv");
                Table randomRows = movies.sampleN(4);

                int j = 1;

                for (Row row : randomRows) {
                    System.out.println(j++ + ". " + row.getString("title"));
                }

                System.out.println("Select a movie (1-4):");
                int movieChoice = scanner.nextInt();

                System.out.println("Rate the movie selected (1.0-5.0):");
                double rating = scanner.nextDouble();

                long timestamp = System.currentTimeMillis() / 1000L;

                // Append to CSV
                File file = new File("./ratings_small.csv");

                try {
                    FileWriter outputfile = new FileWriter(file, true);
                    CSVWriter writer = new CSVWriter(outputfile, ',',
                            CSVWriter.NO_QUOTE_CHARACTER,
                            CSVWriter.DEFAULT_QUOTE_CHARACTER,
                            CSVWriter.DEFAULT_LINE_END);

                    int movieId = randomRows.row(movieChoice - 1).getInt("id");
                    String[] data = {String.valueOf((int) userId), String.valueOf(movieId), String.valueOf(rating), String.valueOf(timestamp)};
                    writer.writeNext(data);
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Recommendation logic
        try {
            DataModel model = new FileDataModel(new File("./ratings_small.csv"));
            UserSimilarity similarity = new LogLikelihoodSimilarity(model);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.4, similarity, model);
            UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);

            List<RecommendedItem> recommendations = recommender.recommend(672, 5);

            System.out.print(recommendations);

            for (RecommendedItem recommendation : recommendations) {
                System.out.println("Recommended Movie ID: " + recommendation.getItemID() + ", Score: " + recommendation.getValue());
            }
        } catch (Exception e) {
            System.out.println("Ocorreram os seguintes erros:");
            e.printStackTrace();
        }
    }
}
