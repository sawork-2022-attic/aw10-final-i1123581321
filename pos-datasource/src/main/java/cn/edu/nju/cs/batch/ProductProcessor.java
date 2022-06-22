package cn.edu.nju.cs.batch;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;

import java.util.regex.Pattern;

public class ProductProcessor implements ItemProcessor<JsonNode, Product> {

    private final Pattern pattern = Pattern.compile("(\\d+(?:\\.\\d+)?)");

    public ProductProcessor() {

    }

    @Override
    public Product process(@NonNull JsonNode jsonNode) throws Exception {
        var name = jsonNode.get("title").asText();

        var image = jsonNode.get("imageURLHighRes");

        String imageUrl = "";
        if (image.isArray() && image.size() > 0){
            imageUrl = image.get(0).asText();
        }

        var priceString = jsonNode.get("price").asText();
        var match = pattern.matcher(priceString);
        if (match.find()){
            var price = Double.parseDouble(match.group(1));
            return new Product(name, price, imageUrl);
        }
        return null;
    }
}
