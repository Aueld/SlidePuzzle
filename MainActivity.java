package com.customslidepuzzle;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

//import androidx.activity.result.ActivityResult;
//import androidx.activity.result.ActivityResultCallback;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity{
	private MainView mainView;
	private Board board;
	private BoardView boardView;
	private TextView moves;
	private Bitmap newImg;

//	private ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
//			new ActivityResultContracts.StartActivityForResult(),
//			new ActivityResultCallback<ActivityResult>() {
//				@Override
//				public void onActivityResult(ActivityResult result) {
//
//					if(result.getResultCode() == RESULT_OK) {
//						try {
//							InputStream in = getContentResolver().openInputStream(result.getData().getData());
//
//							newImg = BitmapFactory.decodeStream(in);
//							in.close();
//
//							boardView.SetImage(newImg);
//							boardView.EndGame(false);
//							board.rearrange();
//							moves.setText(" Count : 0");
//							boardView.invalidate();
//
//						} catch (Exception e) {
//						}
//
//					}
//					else if(result.getResultCode() == RESULT_CANCELED)
//					{
//						Toast.makeText(MainActivity.this, "사진 선택 취소", Toast.LENGTH_LONG).show();
//					}
//				}
//			});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		mainView = new MainView(this);
		setContentView(mainView);

		moves = mainView.getTextView();
		moves.setTextColor(Color.WHITE);
		moves.setTextSize(20);
		newGame();
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());

                    newImg = BitmapFactory.decodeStream(in);
                    in.close();

                    boardView.SetImage(newImg);
                    boardView.EndGame(false);
                    board.rearrange();
                    moves.setText(" Count : 0");
                    boardView.invalidate();

                } catch (Exception e) {
                }

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MainActivity.this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, Menu.NONE, "새로운 게임");
		menu.add(0, 0, Menu.NONE, "이미지 불러오기");

		return true;
	}

	public void newGame() {
		int boardSize = 4;

		board = new Board(boardSize);
		board.addBoardChangeListener(boardChangeListener);
		board.rearrange();
		mainView.removeView(boardView);
		boardView = new BoardView(this, board);
		boardView.EndGame(false);
		mainView.addView(boardView);
		moves.setText(" Count : 0");

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals("새로운 게임")) {
			new AlertDialog.Builder(this)
					.setTitle("새 게임")
					.setMessage("새로운 게임을 시작 할 수 있습니다.")
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int which) {
									board.rearrange();
									moves.setText(" Count : 0");
									boardView.EndGame(false);
									boardView.invalidate();
								}
							})
					.setNegativeButton(android.R.string.no,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int which) {

								}
							}).setIcon(android.R.drawable.ic_dialog_alert)
					.show();
		}

		if (item.getTitle().equals("이미지 불러오기")) {
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);

			//resultLauncher.launch(intent);
            startActivityForResult(intent, 1);
		}

		return super.onOptionsItemSelected(item);
	}

	private final Board.BoardChangeListener boardChangeListener = new Board.BoardChangeListener() {
		public void tileSlid(Place from, Place to, int numOfMoves) {
			moves.setText(" Count : " + Integer.toString(numOfMoves));
		}

		public void solved(int numOfMoves) {
			moves.setText(" Count : " + Integer.toString(numOfMoves));
			boardView.EndGame(true);
			boardView.invalidate();
			Toast.makeText(getApplicationContext(), "퍼즐 완성", Toast.LENGTH_LONG).show();
		}
	};
}
