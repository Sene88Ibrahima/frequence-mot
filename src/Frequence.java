import java.io.*;
import java.util.*;

public class Frequence {
    public static void main(String[] args) throws IOException {
        // Chemin du fichier d'entrée et de sortie
        String inputFilePath = "C:\\purchases.txt";

        String outputFilePath = "purchases_results.txt";

        // Lire les lignes du fichier
        List<String> lines = readFile(inputFilePath);

        // Séparer les mots de chaque ligne
        List<List<String>> documents = splitWords(lines);

        // Calculer les fréquences TF et IDF
        Map<String, Double> tf = calculateTF(documents);
        Map<String, Double> idf = calculateIDF(documents);

        // Calculer TF-IDF
        Map<String, Double> tfidf = calculateTFIDF(tf, idf);

        // Écrire les résultats dans un fichier
        writeResults(tfidf, outputFilePath);

        System.out.println("Résultats TF-IDF écrits dans : " + outputFilePath);
    }

    // Fonction pour lire le fichier
    public static List<String> readFile(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        br.close();
        return lines;
    }


    // Fonction pour diviser les lignes en mots avec filtrage
    public static List<List<String>> splitWords(List<String> lines) {
        List<List<String>> documents = new ArrayList<>();
        for (String line : lines) {
            // Diviser chaque ligne en mots
            List<String> words = Arrays.asList(line.split("\\s+"));
            // Filtrer les nombres et les mots courts
            List<String> filteredWords = new ArrayList<>();
            for (String word : words) {
                if (!word.matches(".*\\d.*") && word.length() > 2) { // Exclure nombres et mots courts
                    filteredWords.add(word.toLowerCase()); // Convertir en minuscule
                }
            }
            documents.add(filteredWords);
        }
        return documents;
    }


    // Fonction pour calculer les fréquences TF
    public static Map<String, Double> calculateTF(List<List<String>> documents) {
        Map<String, Double> tf = new HashMap<>();
        int totalWords = 0;

        for (List<String> doc : documents) {
            for (String word : doc) {
                tf.put(word, tf.getOrDefault(word, 0.0) + 1);
                totalWords++;
            }
        }

        // Normaliser les fréquences par le nombre total de mots
        for (String word : tf.keySet()) {
            tf.put(word, tf.get(word) / totalWords);
        }

        return tf;
    }

    // Fonction pour calculer les IDF
    public static Map<String, Double> calculateIDF(List<List<String>> documents) {
        Map<String, Double> idf = new HashMap<>();
        int totalDocuments = documents.size();

        // Trouver les mots uniques dans chaque document
        for (List<String> doc : documents) {
            Set<String> uniqueWords = new HashSet<>(doc);
            for (String word : uniqueWords) {
                idf.put(word, idf.getOrDefault(word, 0.0) + 1);
            }
        }

        // Calculer l'IDF pour chaque mot
        for (String word : idf.keySet()) {
            idf.put(word, Math.log((double) totalDocuments / idf.get(word)));
        }

        return idf;
    }

    // Fonction pour calculer TF-IDF
    public static Map<String, Double> calculateTFIDF(Map<String, Double> tf, Map<String, Double> idf) {
        Map<String, Double> tfidf = new HashMap<>();

        for (String word : tf.keySet()) {
            tfidf.put(word, tf.get(word) * idf.getOrDefault(word, 0.0));
        }

        return tfidf;
    }

    // Fonction pour écrire les résultats triés dans un fichier
    public static void writeResults(Map<String, Double> tfidf, String outputFilePath) throws IOException {
        // Transformer la map en une liste d'entrées et trier par valeur (TF-IDF décroissant)
        List<Map.Entry<String, Double>> sortedEntries = new ArrayList<>(tfidf.entrySet());
        sortedEntries.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue())); // Tri décroissant

        // Écrire les résultats dans le fichier
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));
        for (Map.Entry<String, Double> entry : sortedEntries) {
            double percentage = entry.getValue() * 100; // Conversion en pourcentage
            writer.write(entry.getKey() + " : " + entry.getValue() + " (" + String.format("%.2f", percentage) + "%)\n");
        }
        writer.close();
    }


}
