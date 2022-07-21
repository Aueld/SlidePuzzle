package com.customslidepuzzle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.PlayGames;
import com.google.android.gms.games.PlayGamesSdk;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
	private MainView mainView;
	private Board board;
	private BoardView boardView;
	private TextView moves;
	private Bitmap newImg;

	private static int RC_SIGN_IN = 9001;
	private static int RC_LEADERBOARD_UI = 9004;
	private GoogleSignInClient mGoogleSignInClient;
	private LeaderboardsClient mLeaderboardsClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PlayGamesSdk.initialize(this);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		mainView = new MainView(this);
		setContentView(mainView);

		moves = mainView.getTextView();
		moves.setTextColor(Color.WHITE);
		moves.setTextSize(20);
		newGame();

		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
				.requestEmail()
				.build();

		mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
		//PlayGames.getLeaderboardsClient(this).submitScore(getString(R.string.leaderboard_low_count), 1337);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RC_SIGN_IN) {
			try {
				Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
				handleSignInResult(task);
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(this, "구글 로그인 실패", Toast.LENGTH_SHORT).show();

			}
		} else if (requestCode == RC_LEADERBOARD_UI){

		} else if (requestCode == 1) {
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

	private void signIn() {
		Intent intent = mGoogleSignInClient.getSignInIntent();
		startActivityForResult(intent, RC_SIGN_IN);
	}

	private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
		GoogleSignInAccount acct = completedTask.getResult();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, Menu.NONE, "새로운 게임");
		menu.add(0, 0, Menu.NONE, "이미지 불러오기");
		menu.add(0, 0, Menu.NONE, "점수 저장하기");

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

	private void reGame() {
		board.rearrange();
		moves.setText(" Count : 0");
		boardView.EndGame(false);
		boardView.invalidate();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getTitle().toString()) {
			case "새로운 게임":
				new AlertDialog.Builder(this)
						.setTitle("새 게임")
						.setMessage("새로운 게임을 시작 할 수 있습니다.")
						.setPositiveButton(android.R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										reGame();
									}
								})
						.setNegativeButton(android.R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {

									}
								}).setIcon(android.R.drawable.ic_dialog_alert)
						.show();
				break;

			case "이미지 불러오기":
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);

				//resultLauncher.launch(intent);
				startActivityForResult(intent, 1);
				break;

			case "점수 저장하기":
				if (boardView.GetEndGame()) {
					new AlertDialog.Builder(this)
							.setTitle("점수 저장")
							.setMessage("점수를 저장할 수 있습니다.\n(취소 시 저장 안하고 새 게임 시작)")
							.setPositiveButton(android.R.string.yes,
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											try {
												signIn();
												leaderBoardRank();

												reGame();
											} catch (Exception e) {
												Toast.makeText(getApplicationContext(), "구글 연동 실패", Toast.LENGTH_SHORT).show();
											}

										}
									})
							.setNegativeButton(android.R.string.no,
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											reGame();
										}
									}).setIcon(android.R.drawable.ic_dialog_alert)
							.show();
				} else {
					Toast.makeText(getApplicationContext(), "퍼즐을 완성해야 점수를 저장할 수 있습니다.", Toast.LENGTH_SHORT).show();
				}
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void leaderBoardRank() {
		PlayGames.getLeaderboardsClient(this).submitScore(getString(R.string.leaderboard_low_count), 1337);
		showLeaderboard();
	}

	private void showLeaderboard(){
		PlayGames.getLeaderboardsClient(this)
				.getLeaderboardIntent(getString(R.string.leaderboard_low_count))
				.addOnSuccessListener(new OnSuccessListener<Intent>() {
					@Override
					public void onSuccess(Intent intent) {
						startActivityForResult(intent, RC_LEADERBOARD_UI);
					}
				});
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

	@Override
	protected void onResume() {
		super.onResume();
	}

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

//		if (item.getTitle().equals("새로운 게임")) {
//			new AlertDialog.Builder(this)
//					.setTitle("새 게임")
//					.setMessage("새로운 게임을 시작 할 수 있습니다.")
//					.setPositiveButton(android.R.string.yes,
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,int which) {
//									board.rearrange();
//									moves.setText(" Count : 0");
//									boardView.EndGame(false);
//									boardView.invalidate();
//								}
//							})
//					.setNegativeButton(android.R.string.no,
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,int which) {
//
//								}
//							}).setIcon(android.R.drawable.ic_dialog_alert)
//					.show();
//		}
//		if (item.getTitle().equals("이미지 불러오기")) {
//			Intent intent = new Intent();
//			intent.setType("image/*");
//			intent.setAction(Intent.ACTION_GET_CONTENT);
//
//			//resultLauncher.launch(intent);
//            startActivityForResult(intent, 1);
//		}

}
