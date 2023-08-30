import java.io.File;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;

import org.apache.log4j.Logger;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;


public class Recommender {

    public static void main(String[] args) {
        PropertyConfigurator.configure("src/main/resources/log4j.properties");
        Logger logger = Logger.getLogger(Recommender.class);
        try {
            DataModel model = new FileDataModel(new File("./ratings_small.csv"));
            UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.7,similarity, model);
            UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);

            List<RecommendedItem> recommendations = recommender.recommend(1, 5);
            for (RecommendedItem recommendation : recommendations) {
                logger.info("Item ID: " + recommendation.getItemID() + ", Score: " + recommendation.getValue());
            }
        } catch (Exception e) {
            System.out.println("Ocorreram os seguintes erros:");
            System.out.print(e);
        }

    }

}
