package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import com.example.patterns.ThreadExample;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

class DictionaryValue {
    private int id;
    private int intKey;
    private String stringKey;
    private String type;
    private String description;

    public DictionaryValue(int id, int intKey, String stringKey, String type, String description) {
        this.id = id;
        this.intKey = intKey;
        this.stringKey = stringKey;
        this.type = type;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public int getIntKey() {
        return intKey;
    }

    public String getStringKey() {
        return stringKey;
    }

    public String getType() {
        return type;
    }
}

class DictionaryFileReader {
    private String filePath;

    public DictionaryFileReader(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getRawFileData() {
        try {
            return Files.readString(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<DictionaryValue> getDictionaryValues() {
        List<DictionaryValue> values = new ArrayList<>();

        String rawData = getRawFileData();

        if (rawData == null || rawData.isEmpty()) {
            return values;
        }

        String[] lines = rawData.split("\\R");

        for (String line : lines) {
            String[] parts = line.split(",");

            if (parts.length == 5) {
                DictionaryValue value = new DictionaryValue(
                        Integer.parseInt(parts[0].trim()),
                        Integer.parseInt(parts[1].trim()),
                        parts[2].trim(),
                        parts[3].trim(),
                        parts[4].trim()
                );

                values.add(value);
            }
        }

        return values;
    }
}

class DictionaryCache {
    private static DictionaryCache instance;
    private List<DictionaryValue> items;

    private DictionaryCache() {
    }

    public static DictionaryCache getInstance() {
        if (instance == null) {
            instance = new DictionaryCache();
        }
        return instance;
    }

    public List<DictionaryValue> getItems() {
        return items;
    }

    public void setItems(List<DictionaryValue> items) {
        this.items = items;
    }
}

class DictionaryCacheRefresher extends Thread {

    @Override
    public void run() {
        while (true) {
            try {
                DictionaryFileReader reader = new DictionaryFileReader("src/main/resources/dictionaries.csv");
                List<DictionaryValue> values = reader.getDictionaryValues();

                DictionaryCache cache = DictionaryCache.getInstance();
                cache.setItems(values);

                System.out.println("\n=== CACHE ODŚWIEŻONY ===");
                System.out.println("Liczba rekordów: " + values.size());

                for (DictionaryValue value : values) {
                    System.out.println(
                            value.getId() + " | " + value.getIntKey() + " | " +
                                    value.getStringKey() + " | " + value.getType() + " | " +
                                    value.getDescription()
                    );
                }
                Thread.sleep(5000);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

public class MainProgram {

    public static void main(String[] args) {
        /** ✅ Krok 1: Utwórz klasę DictionaryValue z odpowiednimi polami i metodami */

        DictionaryValue testValue = new DictionaryValue(1, 2, "POM", "Region", "Woj. Pomorskie");
        if (testValue != null && testValue.getIntKey() == 2 && "POM".equals(testValue.getStringKey())) {
            System.out.println("✅ DictionaryValue utworzony poprawnie: " + testValue);
        } else {
            System.out.println("❌ Sprawdź konstruktor oraz gettery/settery w klasie DictionaryValue.");
            return;
        }

        /** ✅ Krok 2: Utwórz klasę DictionaryFileReader, która odczytuje dane z pliku CSV */

        DictionaryFileReader reader = new DictionaryFileReader("src/main/resources/dictionaries.csv");
        if (reader != null) {
            System.out.println("✅ Reader zainicjalizowany.");
        } else {
            System.out.println("❌ Czy stworzyłeś klasę DictionaryFileReader z odpowiednim konstruktorem?");
            return;
        }

        /** ✅ Krok 3: Dodaj metodę getRawFileData() do klasy reader */

        String rawData = reader.getRawFileData();
        if (rawData != null && rawData.contains("Region")) {
            System.out.println("Zawartość pliku:\n" + rawData);
        } else {
            System.out.println("❌ Czy dodałeś metodę getRawFileData()? Czy poprawnie wczytuje dane z pliku?");
            return;
        }

        /** ✅ Krok 4: Dodaj metodę getDictionaryValues() która zwraca listę DictionaryValue */

        List<DictionaryValue> dictionaryValues = reader.getDictionaryValues();
        if (dictionaryValues != null && dictionaryValues.size() == 10) {
            System.out.println("✅ Załadowano 10 rekordów z pliku CSV.");
        } else {
            System.out.println("❌ Czy dodałeś metodę getDictionaryValues() i poprawnie sparsowałeś dane?");
            return;
        }

        /** ✅ Krok 5: Stwórz klasę DictionaryCache jako Singleton i przekaż do niej dane */

        DictionaryCache cache = DictionaryCache.getInstance();
        if (cache != null) {
            cache.setItems(dictionaryValues);
            System.out.println("✅ Dane zapisane w cache.");

            /** 🔍 Sprawdzenie: czy dane można pobrać z cache */
            List<DictionaryValue> cachedValues = cache.getItems();
            if (cachedValues != null && cachedValues.size() == 10) {
                System.out.println("✅ Dane poprawnie pobrane z cache (" + cachedValues.size() + " elementów).");
            } else {
                System.out.println("❌ Czy metoda getItems() w klasie DictionaryCache działa poprawnie?");
                return;
            }
        } else {
            System.out.println("❌ Czy poprawnie zaimplementowałeś klasę Singleton (DictionaryCache)?");
            return;
        }

        /** ✅ Krok 6: Wątek odświeżający dane w tle (DictionaryCacheRefresher) */
        /** Dodaj do klasy DictionaryCacheRefresher logikę, która przy każdej aktualizacji danych
         * nie tylko wypisze liczbę rekordów, ale także pełną zawartość cache’a – linia po linii.
         */
        
        DictionaryCacheRefresher refresher = new DictionaryCacheRefresher();
        if (refresher != null) {
            refresher.start();
            System.out.println("✅ Wątek odświeżający został uruchomiony.");
        } else {
            System.out.println("❌ Czy stworzyłeś klasę dziedziczącą po Thread o nazwie DictionaryCacheRefresher?");
        }

        /** 📝 Uwaga dla studentów:
         * Po uruchomieniu aplikacji spróbuj ręcznie zmodyfikować plik 'dictionaries.csv'
         * (np. dodaj nowy wiersz lub usuń istniejący), aby zobaczyć, czy wątek poprawnie odświeża dane w cache.
         * Wątek powinien wypisywać zaktualizowaną liczbę rekordów za każdym odświeżeniem.
         */
    }
}
