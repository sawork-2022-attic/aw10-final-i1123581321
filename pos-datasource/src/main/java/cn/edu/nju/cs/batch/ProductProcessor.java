package cn.edu.nju.cs.batch;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.batch.item.ItemProcessor;

import java.util.regex.Pattern;
import java.util.zip.DeflaterOutputStream;

public class ProductProcessor implements ItemProcessor<JsonNode, Product> {

    private final Pattern pattern = Pattern.compile("(\\d+(?:\\.\\d+)?)");

    public ProductProcessor() {

    }

    @Override
    public Product process(JsonNode jsonNode) throws Exception {
        if (jsonNode == null){
            return null;
        }
        var name = jsonNode.get("title").asText();

        var image = jsonNode.get("imageURLHighRes");

        String imageUrl = "";
        if (image.isArray() && image.size() > 0){
            imageUrl = image.get(0).asText();
        }

        double price = 0;
        var priceString = jsonNode.get("price").asText();
        var match = pattern.matcher(priceString);
        if (match.find()){
            price = Double.parseDouble(match.group(1));
        }
        return new Product(name, price, imageUrl);
    }
}
