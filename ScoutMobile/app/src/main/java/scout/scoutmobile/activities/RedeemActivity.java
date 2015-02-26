package scout.scoutmobile.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import scout.scoutmobile.R;
import scout.scoutmobile.constants.Consts;
import scout.scoutmobile.utils.GeneralUtils;
import scout.scoutmobile.utils.Logger;


public class RedeemActivity extends Activity {

    Logger mLogger = new Logger("RedeemActivity");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);

        String rewardID = getIntent().getStringExtra(Consts.REWARD_ID);
        String customerID = getIntent().getStringExtra(Consts.CUSTOMER_ID);

        generateQR(rewardID, customerID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GeneralUtils.verifyUserLoggedIn(this);
    }

    private void generateQR(String rewardID, String customerID) {
        ImageView qrImageView = (ImageView) findViewById(R.id.qrImage);
        QRCodeWriter qrWriter = new QRCodeWriter();

        int dimension = 100;
        int black = 0xFF000000;
        int white = 0xFFFFFFFF;

        try {
            BitMatrix bitMatrix = qrWriter.encode(rewardID + customerID,
                            BarcodeFormat.QR_CODE, dimension, dimension);

            int[] pixels = new int[dimension * dimension];
            for (int y = 0; y < dimension; ++y) {
                for (int x = 0; x < dimension; ++x) {
                    pixels[y * dimension + x] = bitMatrix.get(x, y) ? black : white;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(dimension, dimension, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, dimension, 0, 0, dimension, dimension);
            qrImageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            mLogger.logError(e);
        }
    }
}
