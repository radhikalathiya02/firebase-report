package com.firebase.report.firebase_report;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import java.io.FileInputStream;
import java.time.LocalDate;
import java.util.*;

public class FirebaseReport {

	static final String SERVICE_ACCOUNT_PATH = "service-account.json";

    public static void main(String[] args) throws Exception {

        // Initialize Firebase
        FileInputStream serviceAccount = new FileInputStream(SERVICE_ACCOUNT_PATH);
        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setProjectId("limad-cee0a")
            .build();
        FirebaseApp.initializeApp(options);
        System.out.println("Firebase initialized");

        // Generate report
        String report = generateReport();
        System.out.println(report);

        // Send to Zulip
        sendToZulip(report);
    }

    static String generateReport() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        StringBuilder report = new StringBuilder();

        report.append("Keep My Notes - Daily Report\n");
        report.append("Date: " + yesterday + "\n\n");

        // ---- Crash Free ----
        // TODO: Replace with real Crashlytics data
        report.append("Crash Free\n");
        report.append("- v[1.9] : 98.8% crash free\n");
        report.append("- v[1.8] : 100% crash free\n\n");

        // ---- Users Last 28 Days ----
        // TODO: Replace with real Analytics data
//        report.append("Users (Last 28 Days)\n");
//        report.append("- v[1.9] : 10 users\n");
//        report.append("- v[1.8] : 89 users\n\n");

        // ---- Yesterday Stats ----
        // TODO: Replace with real Analytics data
//        report.append("Yesterday Stats\n");
//        report.append("- v[1.9] : 8 users | 2 crashes\n");
//        report.append("- v[1.8] : 89 users | 0 crashes\n\n");

        //report.append("Total Crashes Yesterday: 2\n");
        //report.append("Crashes detected - check Crashlytics\n\n");

        //report.append("Links\n");
        //report.append("Crashlytics: https://console.firebase.google.com/project/limad-cee0a/crashlytics/app/android:keepmynotes.notepad.taker.notelist/issues\n");
        //report.append("Analytics: https://analytics.google.com/analytics/web/#/a281048546p516785701/reports/\n");

        return report.toString();
    }

//    static String generateRealReport() throws Exception {
//        GoogleCredentials credentials = GoogleCredentials
//            .fromStream(new FileInputStream(SERVICE_ACCOUNT_PATH))
//            .createScoped(
//                "https://www.googleapis.com/auth/analytics.readonly"
//            );
//        BetaAnalyticsDataSettings settings = BetaAnalyticsDataSettings
//            .newBuilder()
//            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
//            .build();
//        try (BetaAnalyticsDataClient client =
//                BetaAnalyticsDataClient.create(settings)) {
//
//            // Yesterday stats
//            RunReportRequest requestYesterday = RunReportRequest.newBuilder()
//                .setProperty("properties/" + GA_PROPERTY_ID)
//                .addDateRanges(DateRange.newBuilder()
//                    .setStartDate("yesterday")
//                    .setEndDate("yesterday"))
//                .addDimensions(Dimension.newBuilder().setName("appVersion"))
//                .addMetrics(Metric.newBuilder().setName("activeUsers"))
//                .addMetrics(Metric.newBuilder().setName("crashAffectedUsers"))
//                .addMetrics(Metric.newBuilder().setName("crashFreeUsersRate"))
//                .addOrderBys(OrderBy.newBuilder()
//                    .setMetric(OrderBy.MetricOrderBy.newBuilder()
//                        .setMetricName("activeUsers"))
//                    .setDesc(true))
//                .setLimit(2)
//                .build();
//            RunReportResponse responseYesterday =
//                client.runReport(requestYesterday);
//
//            // 28 days users
//            RunReportRequest request28 = RunReportRequest.newBuilder()
//                .setProperty("properties/" + GA_PROPERTY_ID)
//                .addDateRanges(DateRange.newBuilder()
//                    .setStartDate("28daysAgo")
//                    .setEndDate("today"))
//                .addDimensions(Dimension.newBuilder().setName("appVersion"))
//                .addMetrics(Metric.newBuilder().setName("activeUsers"))
//                .addOrderBys(OrderBy.newBuilder()
//                    .setMetric(OrderBy.MetricOrderBy.newBuilder()
//                        .setMetricName("activeUsers"))
//                    .setDesc(true))
//                .setLimit(2)
//                .build();
//            RunReportResponse response28 = client.runReport(request28);
//        }
//    }

    static void sendToZulip(String message) throws Exception {

        javax.net.ssl.TrustManager[] trustAllCerts =
            new javax.net.ssl.TrustManager[]{
            new javax.net.ssl.X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
            }
        };

        javax.net.ssl.SSLContext sslContext =
            javax.net.ssl.SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        org.apache.http.conn.ssl.SSLConnectionSocketFactory sslFactory =
            new org.apache.http.conn.ssl.SSLConnectionSocketFactory(
                sslContext,
                org.apache.http.conn.ssl.NoopHostnameVerifier.INSTANCE
            );

        try (CloseableHttpClient client = HttpClients.custom()
                .setSSLSocketFactory(sslFactory)
                .build()) {

            HttpPost post = new HttpPost(
                "https://103.254.172.165:8080/api/v1/messages"
            );

            String credentials = "radhikalathiya-bot@103.254.172.165"
                + ":" + "5EQMWMXAfXLVTVoGUDwnnyQaljIsN9PX";
            String encoded = java.util.Base64.getEncoder()
                .encodeToString(credentials.getBytes());
            post.setHeader("Authorization", "Basic " + encoded);
            post.setHeader("Content-Type",
                "application/x-www-form-urlencoded");

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("type", "private"));
            params.add(new BasicNameValuePair("to", "user969@103.254.172.165"));
            params.add(new BasicNameValuePair("content", message));
            post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            org.apache.http.HttpResponse response = client.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = new String(
                response.getEntity().getContent().readAllBytes());

            System.out.println("Zulip response code: " + statusCode);
            System.out.println("Zulip response body: " + responseBody);

            if (statusCode == 200) {
                System.out.println("Report sent to Zulip!");
            } else {
                System.out.println("Zulip error: " + statusCode);
            }
        }
    }
}