import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Menu {
    public static void main(String[] args) throws IOException, InterruptedException {
        int opcion = 0;
        Scanner teclado = new Scanner(System.in);
        System.out.println("********************************************");
        System.out.println("Bienvenido al Conversor de Moneda");
        String menu = """
                *** Escriba el numero de la opcion deseada
                1 - Dolar => Peso Argentino
                2 - Peso Argentino => Dolar
                3 - Dolar => Real Brasileño
                4 - Real Brasileño => Dolar
                5 - Dolar => Peso Colombiano
                6 - Peso Colombiano => Dolar
                7 - Salir
                Elija una opcion valida
                """;
        System.out.println("\n********************************************");

        while (opcion != 7) {
            System.out.println(menu);
            opcion = teclado.nextInt();

            switch (opcion) {
                case 1:
                    convertirMoneda("USD", "ARS");
                    break;
                case 2:
                    convertirMoneda("ARS", "USD");
                    break;
                case 3:
                    convertirMoneda("USD", "BRL");
                    break;
                case 4:
                    convertirMoneda("BRL", "USD");
                    break;
                case 5:
                    convertirMoneda("USD", "COP");
                    break;
                case 6:
                    convertirMoneda("COP", "USD");
                    break;
                case 7:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opcion invalida. Por favor, intente nuevamente.");
                    break;
            }
        }
    }

    private static void convertirMoneda(String fromCurrency, String toCurrency) throws IOException, InterruptedException {
        Scanner lectura = new Scanner(System.in);
        System.out.println("Escriba el valor que desea convertir: ");
        double valor = lectura.nextDouble();

        String direccion = "https://v6.exchangerate-api.com/v6/6b78ed17463106be5660263c/latest/" + fromCurrency;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(direccion))
                .build();

        HttpResponse<String> response = client
                .send(request, HttpResponse.BodyHandlers.ofString());

        String json = response.body();
        double tasa = obtenerTasaCambio(json, toCurrency);

        if (tasa != 0) {
            double resultado = valor * tasa;
            System.out.printf("El valor de %.2f %s en %s es: %.2f\n", valor, fromCurrency, toCurrency, resultado);
        } else {
            System.out.println("Error obteniendo la tasa de cambio.");
        }
    }

    private static double obtenerTasaCambio(String json, String toCurrency) {
        int startIndex = json.indexOf(toCurrency) + 5;
        if (startIndex == 4) return 0;

        int endIndex = json.indexOf(",", startIndex);
        if (endIndex == -1) {
            endIndex = json.indexOf("}", startIndex);
        }

        try {
            return Double.parseDouble(json.substring(startIndex, endIndex));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }
}

