package com.example.herbidentifier;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;
import android.graphics.Bitmap.Config;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import org.tensorflow.lite.Interpreter;
import android.provider.MediaStore;

public class ResultActivity extends AppCompatActivity {

    private static final int IMAGE_SIZE = 150;

    private Interpreter tflite;
    private List<String> labels;
    private TextView tvResult, tvDefinition, tvBenefit;
    private ImageView imageView;

    // Data definisi terpisah (diperpanjang)
    private final String[] definitions = {
            "Daun Bidara: Tanaman Bidara (Ziziphus mauritiana), juga dikenal sebagai Indian Jujube, adalah pohon kecil atau semak berduri yang tumbuh subur di daerah tropis dan subtropis seperti Asia, Afrika, dan Australia. Daunnya berbentuk oval, hijau mengkilap, dan memiliki tekstur sedikit berbulu di bagian bawah. Daun bidara dikenal dalam berbagai tradisi karena sifat obat dan spiritualnya.",
            "Daun Binahong: Tanaman Binahong (Anredera cordifolia), sering disebut juga Gondola Basella atau Heartleaf Mignonette Vine, adalah tanaman merambat yang cepat tumbuh. Berasal dari Amerika Selatan, tanaman ini telah menyebar luas di wilayah tropis dan subtropis. Daunnya berbentuk hati, tebal, berdaging, dan berwarna hijau gelap. Binahong dikenal kaya akan saponin, flavonoid, dan polifenol.",
            "Daun Gatal: Daun Gatal (Laportea decumana), atau yang dikenal juga sebagai Jelatang Gajah atau Stinging Nettle dari Indonesia, adalah tanaman yang terkenal karena kemampuannya menyebabkan sensasi gatal dan terbakar yang kuat saat bersentuhan dengan kulit. Tanaman ini memiliki bulu-bulu halus yang mengandung zat iritan, namun di balik itu, daunnya juga dimanfaatkan dalam pengobatan tradisional.",
            "Daun Jambu: Daun Jambu Biji (Psidium guajava) berasal dari pohon jambu biji, tanaman buah tropis yang umum ditemukan di berbagai belahan dunia. Daunnya berbentuk oval, berwarna hijau terang, dan memiliki aroma khas yang kuat. Daun jambu biji mengandung senyawa aktif seperti flavonoid, tanin, dan polifenol yang memberikan berbagai efek farmakologis.",
            "Daun Kelor: Daun Kelor (Moringa oleifera) adalah bagian dari pohon kelor, yang sering disebut 'pohon keajaiban' karena kandungan nutrisinya yang luar biasa tinggi. Tanaman ini tumbuh subur di daerah tropis dan subtropis, dengan daun-daun kecil majemuk berwarna hijau terang yang tersusun rapi seperti pinus. Kelor kaya akan vitamin (A, C, E, K), mineral (kalsium, kalium, zat besi), protein, dan antioksidan.",
            "Daun Kirinyuh: Daun Kirinyuh (Chromolaena odorata), juga dikenal sebagai Siam Weed atau Communist Weed, adalah gulma invasif yang tumbuh sangat cepat di daerah tropis dan subtropis. Meskipun sering dianggap hama, daunnya memiliki khasiat obat dalam pengobatan tradisional, terutama untuk menghentikan pendarahan. Daun kirinyuh memiliki tekstur sedikit kasar dan aroma khas saat diremas.",
            "Daun Miana: Daun Miana (Coleus scutellarioides), atau yang populer sebagai tanaman hias 'Coleus', adalah tanaman dengan daun berwarna-warni yang mencolok. Selain keindahan estetikanya, daun miana juga dikenal luas dalam pengobatan tradisional sebagai herbal dengan berbagai khasiat. Daunnya bervariasi dalam bentuk dan warna, seringkali kombinasi hijau, merah, ungu, atau kuning.",
            "Daun Pepaya: Daun Pepaya (Carica papaya) adalah daun besar berlobus yang berasal dari pohon pepaya, tanaman buah tropis yang sangat populer. Daun ini memiliki rasa pahit yang khas, namun kaya akan enzim papain, antioksidan, dan senyawa fitokimia lainnya. Daun pepaya sering diolah menjadi masakan atau ramuan herbal karena kandungan nutrisinya yang tinggi.",
            "Daun Salam: Daun Salam (Syzygium polyanthum) adalah daun aromatik yang sangat populer di masakan Asia Tenggara, terutama Indonesia, sebagai bumbu penyedap alami. Selain digunakan dalam kuliner, daun salam juga memiliki sejarah panjang dalam pengobatan tradisional karena kandungan flavonoid, tanin, dan minyak esensialnya. Daunnya berbentuk oval memanjang dan berwarna hijau gelap.",
            "Daun Singkong: Daun Singkong (Manihot esculenta) adalah daun dari tanaman singkong, salah satu tanaman pangan pokok di banyak negara tropis. Daun ini memiliki bentuk jari-jari dengan lima hingga tujuh lobus. Meskipun umbinya dikenal sebagai sumber karbohidrat, daun singkong juga merupakan sumber protein, serat, vitamin, dan mineral yang sangat baik, menjadikannya sayuran bergizi tinggi.",
            "Daun Sirih: Daun Sirih (Piper betle) adalah tanaman merambat tropis yang berasal dari Asia Tenggara. Daunnya berbentuk hati, berwarna hijau gelap, dan memiliki aroma yang kuat serta rasa pedas yang khas. Sirih telah digunakan selama berabad-abad dalam tradisi kuno, baik untuk keperluan sosial-budaya (seperti mengunyah sirih) maupun sebagai ramuan obat tradisional karena sifat antiseptik dan antimikrobanya.",
            "Daun Sirsak: Daun Sirsak (Annona muricata) berasal dari pohon sirsak, tanaman buah tropis yang dikenal dengan buahnya yang manis dan sedikit asam. Daun sirsak berbentuk oval, berwarna hijau gelap, dan memiliki tekstur sedikit tebal. Daun ini telah menarik perhatian dalam penelitian ilmiah karena kandungan senyawa fitokimia yang beragam, terutama acetogenins, yang sedang diteliti untuk potensi khasiat obatnya."
    };

    // Data manfaat terpisah (diperpanjang)
    private final String[] benefits = {
            "Manfaat Daun Bidara: Secara tradisional digunakan untuk ruqyah (pengobatan spiritual) dan sebagai obat luar untuk luka, gatal-gatal, serta masalah kulit lainnya. Kandungan anti-inflamasi dan antioksidannya juga mendukung penyembuhan dan perlindungan sel tubuh.",
            "Manfaat Daun Binahong: Dikenal sangat efektif dalam mempercepat penyembuhan luka baik luar maupun dalam, mengurangi peradangan, dan meningkatkan sirkulasi darah. Daun ini juga digunakan untuk mengatasi masalah pencernaan dan menjaga stamina tubuh.",
            "Manfaat Daun Gatal: Meskipun menyebabkan gatal, getah dan ekstrak daunnya secara tradisional digunakan untuk meredakan nyeri otot, sendi (rheumatism), dan peradangan. Di Papua, getahnya dipercaya dapat menghangatkan tubuh dan meredakan demam.",
            "Manfaat Daun Jambu: Sangat populer untuk mengatasi diare karena kandungan taninnya yang membantu mengencangkan saluran pencernaan. Selain itu, daun ini juga kaya antioksidan, dapat membantu mengontrol kadar gula darah, dan memiliki sifat antibakteri.",
            "Manfaat Daun Kelor: Dijuluki 'superfood' karena kaya nutrisi, termasuk vitamin A, C, E, K, kalsium, kalium, dan zat besi. Kelor sangat bermanfaat untuk meningkatkan kekebalan tubuh, menjaga kesehatan tulang, mata, dan kulit, serta membantu menurunkan tekanan darah tinggi, mengurangi kolesterol, dan mengatur kadar gula darah.",
            "Manfaat Daun Kirinyuh: Manfaat utamanya adalah sebagai hemostatik alami, sangat efektif untuk menghentikan pendarahan pada luka baru. Selain itu, ia juga memiliki sifat antiseptik dan anti-inflamasi yang mendukung proses penyembuhan luka.",
            "Manfaat Daun Miana: Digunakan dalam pengobatan tradisional untuk mengobati peradangan, demam, asma, dan batuk. Senyawa aktifnya juga memiliki potensi sebagai antibakteri dan antioksidan, mendukung kesehatan sistem pernapasan dan kekebalan.",
            "Manfaat Daun Pepaya: Terkenal untuk melancarkan pencernaan berkat enzim papain yang tinggi. Selain itu, daun ini juga dapat membantu meningkatkan jumlah trombosit pada kasus demam berdarah, memiliki sifat anti-malaria, dan anti-inflamasi.",
            "Manfaat Daun Salam: Sering digunakan untuk membantu menurunkan kadar kolesterol dan asam urat dalam darah. Daun ini juga memiliki sifat antioksidan, anti-inflamasi, dan dipercaya dapat membantu mengontrol tekanan darah dan kadar gula darah.",
            "Manfaat Daun Singkong: Merupakan sumber protein nabati yang baik, serat, vitamin A, dan zat besi. Daun ini bermanfaat untuk menambah energi, mencegah anemia, serta menjaga kesehatan pencernaan dan kekebalan tubuh.",
            "Manfaat Daun Sirih: Dikenal sebagai antiseptik dan antimikroba alami yang kuat. Sangat efektif untuk menjaga kebersihan mulut, mengatasi bau badan, mengobati luka ringan, serta meredakan gatal dan peradangan pada kulit.",
            "Manfaat Daun Sirsak: Menarik perhatian dalam penelitian karena potensi sifat anti-kankernya (melalui senyawa acetogenins), anti-inflamasi, dan antimikroba. Secara tradisional digunakan untuk mengatasi demam, nyeri, dan masalah pencernaan."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvResult = findViewById(R.id.tvResult);
        tvDefinition = findViewById(R.id.tvDefinition);
        tvBenefit = findViewById(R.id.tvBenefit);
        imageView = findViewById(R.id.imageView);

        try {
            tflite = new Interpreter(loadModelFile());
            labels = loadLabels();
            Log.d("ResultActivity", "Model dan label berhasil dimuat.");
        } catch (Exception e) {
            tvResult.setText("Error: Model gagal dimuat. " + e.getMessage());
            Log.e("ResultActivity", "Gagal memuat model atau label", e);
            return;
        }

        try {
            String imageUriStr = getIntent().getStringExtra("imageUri");
            if (imageUriStr == null || imageUriStr.isEmpty()) {
                tvResult.setText("Error: URI gambar tidak ditemukan.");
                Log.e("ResultActivity", "URI gambar kosong atau null");
                return;
            }

            Uri imageUri = Uri.parse(imageUriStr);
            Bitmap bitmap = null;

            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                if (bitmap == null) {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                }
            } catch (Exception e) {
                Log.e("ResultActivity", "Gagal memuat bitmap dari URI setelah beberapa percobaan", e);
                tvResult.setText("Error: Gagal membaca gambar dari URI.");
                return;
            }

            if (bitmap != null) {
                if (bitmap.getConfig() == Config.HARDWARE) {
                    bitmap = bitmap.copy(Config.ARGB_8888, true);
                    Log.d("ResultActivity", "Mengkonversi bitmap HARDWARE ke ARGB_8888 untuk akses piksel.");
                } else if (!bitmap.isMutable()) {
                    bitmap = bitmap.copy(Config.ARGB_8888, true);
                    Log.d("ResultActivity", "Mengkonversi bitmap immutable (non-hardware) ke ARGB_8888 untuk akses piksel.");
                }

                imageView.setImageBitmap(bitmap);

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, false);

                classifyImage(scaledBitmap);
            } else {
                tvResult.setText("Error: Gambar daun tidak dapat diakses atau null.");
                Log.e("ResultActivity", "Bitmap yang dihasilkan null setelah pembacaan URI");
            }

        } catch (Exception e) {
            tvResult.setText("Error umum saat memproses gambar: " + e.getMessage());
            Log.e("ResultActivity", "Error umum di onCreate saat memproses gambar", e);
            e.printStackTrace();
        }
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private List<String> loadLabels() throws IOException {
        List<String> labelList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(getAssets().open("labels.txt")));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    private void classifyImage(Bitmap bitmap) {
        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(4 * IMAGE_SIZE * IMAGE_SIZE * 3);
        inputBuffer.order(ByteOrder.nativeOrder());

        int[] pixels = new int[IMAGE_SIZE * IMAGE_SIZE];
        bitmap.getPixels(pixels, 0, IMAGE_SIZE, 0, 0, IMAGE_SIZE, IMAGE_SIZE);

        for (int pixel : pixels) {
            int r = (pixel >> 16) & 0xFF;
            int g = (pixel >> 8) & 0xFF;
            int b = pixel & 0xFF;

            inputBuffer.putFloat(r / 255.0f);
            inputBuffer.putFloat(g / 255.0f);
            inputBuffer.putFloat(b / 255.0f);
        }

        float[][] output = new float[1][labels.size()];
        tflite.run(inputBuffer, output);

        int maxIdx = 0;
        float maxConfidence = 0;
        for (int i = 0; i < labels.size(); i++) {
            if (output[0][i] > maxConfidence) {
                maxConfidence = output[0][i];
                maxIdx = i;
            }
        }

        String label = labels.get(maxIdx);
        float confidence = maxConfidence * 100;

        tvResult.setText("Jenis Daun: " + label + "\nPersentase: " + String.format("%.2f", confidence) + "%");

        // Menampilkan definisi dan manfaat secara terpisah
        if (maxIdx >= 0 && maxIdx < definitions.length) {
            tvDefinition.setText("Definisi: " + definitions[maxIdx]);
        } else {
            tvDefinition.setText("Tidak ada informasi definisi untuk daun ini.");
        }

        if (maxIdx >= 0 && maxIdx < benefits.length) {
            tvBenefit.setText(benefits[maxIdx]);
        } else {
            tvBenefit.setText("Tidak ada informasi manfaat untuk daun ini.");
        }

        if (confidence < 5.0f) {
            tvResult.setText("Tidak Dikenali");
            tvDefinition.setText("Maaf, daun tidak dapat diidentifikasi dengan jelas.");
            tvBenefit.setText("Coba ambil gambar lain yang lebih jelas.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tflite != null) {
            tflite.close();
            tflite = null;
            Log.d("ResultActivity", "Interpreter TensorFlow Lite ditutup.");
        }
    }
}