package ru.slepova;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    static CrptApi crptApi = new CrptApi(TimeUnit.SECONDS, 3);

    public static void main(String[] args) {
        Thread myThread1 = new Thread(new MyThread());
        Thread myThread2 = new Thread(new MyThread());
        Thread myThread3 = new Thread(new MyThread());
        Thread myThread4 = new Thread(new MyThread());
        Thread myThread5 = new Thread(new MyThread());
        Thread myThread6 = new Thread(new MyThread());
        Thread myThread7 = new Thread(new MyThread());
        Thread myThread8 = new Thread(new MyThread());
        myThread1.start();
        myThread2.start();
        myThread3.start();
        myThread4.start();
        myThread5.start();
        myThread6.start();
        myThread7.start();
        myThread8.start();
    }

    static class MyThread implements Runnable {

        @Override
        public void run() {
            try {
                createDocument();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void createDocument() throws IOException, InterruptedException {
            CrptApi.Product product = new CrptApi.Product(
                    "certificate document",
                    LocalDate.of(2020, 1, 21),
                    "20",
                    "555555555",
                    "66666666666",
                    LocalDate.of(2021, 2, 20),
                    "1",
                    "2",
                    "3");
            List<CrptApi.Product> products = new ArrayList<>();
            products.add(product);
            CrptApi.Description description = new CrptApi.Description("777");
            CrptApi.Document document = new CrptApi.Document(
                    description,
                    "1",
                    "status",
                    CrptApi.DocType.LP_INTRODUCE_GOODS,
                    true,
                    "111111",
                    "222222",
                    "33333333",
                    LocalDate.of(2022, 3, 23),
                    "production type",
                    products,
                    LocalDate.of(2021, 3, 15),
                    "9"
            );
            HttpResponse<String> response = crptApi.createDocument(document, "sign");
            System.out.println(response.body());
        }
    }
}