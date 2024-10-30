//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ConversorMoedas {
    public static final String API_URL = "https://v6.exchangerate-api.com/v6/966ef38abfe8e8135e6d8caf/latest/USD";
    public static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean continuar = true;

        while (continuar) {
            exibirMenu();
            int opcao = lerOpcaoValida();

            if (opcao == 8) {
                continuar = false;
                System.out.println("Obrigado por utilizar o conversor de moedas!");
            } else if (opcao >= 1 && opcao <= 7) {
                String[] moedas = obterMoedasParaConversao(opcao);
                converterMoedas(moedas[0], moedas[1]);
            } else {
                System.out.println("Opção inválida. Por favor, tente novamente.");
            }
        }
        scanner.close();
    }

    private static double obterTaxaCambio(String de, String para) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Erro HTTP: " + conn.getResponseCode());
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder responseBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }

            String response = responseBuilder.toString();

            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
            JsonObject taxas = jsonObject.getAsJsonObject("conversion_rates");

            double taxaDe = taxas.get(de).getAsDouble();
            double taxaPara = taxas.get(para).getAsDouble();

            return taxaPara / taxaDe;
        }
    }

    private static void exibirMenu() {
        System.out.println("Escolha a conversão de moeda:");
        System.out.println("1. Dólar para Peso Argentino");
        System.out.println("2. Peso Argentino para Dólar");
        System.out.println("3. Dólar para Real Brasileiro");
        System.out.println("4. Real Brasileiro para Dólar");
        System.out.println("5. Peso Colombiano para Dólar");
        System.out.println("6. Euro para Dólar");
        System.out.println("7. Yuan Chinês para Dólar");
        System.out.println("8. Sair");
    }

    private static int lerOpcaoValida() {
        System.out.print("Digite a opção desejada: ");
        while (!scanner.hasNextInt()) {
            System.out.println("Por favor, digite um número válido.");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private static String[] obterMoedasParaConversao(int opcao) {
        switch (opcao) {
            case 1: return new String[] {"USD", "ARS"};
            case 2: return new String[] {"ARS", "USD"};
            case 3: return new String[] {"USD", "BRL"};
            case 4: return new String[] {"BRL", "USD"};
            case 5: return new String[] {"COP", "USD"};
            case 6: return new String[] {"EUR", "USD"};
            case 7: return new String[] {"CNY", "USD"};
            default: return new String[] {"USD", "USD"};
        }
    }

    private static void converterMoedas(String de, String para) {
        try {
            System.out.print("Digite o valor a ser convertido: ");
            double valor = scanner.nextDouble();
            double taxa = obterTaxaCambio(de, para);
            double resultado = valor * taxa;
            System.out.printf("Resultado: %.2f %s\n", resultado, para);
        } catch (Exception e) {
            System.out.println("Erro ao obter a taxa de câmbio: " + e.getMessage());
        }
    }
}