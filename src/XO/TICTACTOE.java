package XO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;
public class TICTACTOE {
	private static int second = 0;
	private static Timer timer = new Timer();
	private static JLabel jLabel;
	private static JButton button;
	private static Board board;
	private static NumberFormat format;
	public static void main(String[] args) {	
		int a[] = new int [1];
		board = new Board();
		board.setEndGameListener(new EndGameListener() {
			@Override	
			public void end(String player, int st) {
				if(st == Board.ST_WIN) {
					JOptionPane.showMessageDialog(null, "nguời chơi "+player+" thắng");
					stopGame();
				}else if(st == Board.ST_DRAW) {
					JOptionPane.showMessageDialog(null, "game hoà");
					stopGame();
				}
			}
		});
		
		JPanel jpanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(jpanel, BoxLayout.Y_AXIS);
		jpanel.setLayout(boxLayout);
		
		board.setPreferredSize(new Dimension(750,750));
		FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER,0,0);
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(flowLayout);
		button = new JButton("Start");
		jLabel = new JLabel("00:00");
		bottomPanel.setPreferredSize(new Dimension(750,50));
		bottomPanel.setBackground(Color.GREEN);
		
		
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                if(button.getText().equals("Start")){
                	board.soundStart();
                    startGame();
                }else{
                    stopGame();
                }
            }
        });
		
		jpanel.add(board);
		jpanel.add(bottomPanel);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		bottomPanel.add(jLabel);
		bottomPanel.add(button);
		JFrame jFrame = new JFrame("super tic tac toe");
		
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setResizable(true);
		jFrame.add(jpanel);
		
		int midDai = (int) (dimension.getHeight()/2 - (board.getPreferredSize().height/2 + bottomPanel.getPreferredSize().height));
		int midRong = (int) (dimension.getWidth()/2 - board.getPreferredSize().width/2);
		
		jFrame.setLocation(midRong, midDai);
		jFrame.pack();
		//show ra frame
		jFrame.setVisible(true);
		
		
	}
	private static void startGame() {
		
		//who's first?
		int choice = JOptionPane.showConfirmDialog(null, "O: yes \t X: no", "who's first",JOptionPane.YES_NO_OPTION);
		
		board.reset();
		String currentPlayer = (choice ==0) ? Cell.O_VALUE : Cell.X_VALUE;
		board.setCurrentPlayer(currentPlayer);
		//dem nguoc	
		second = 0;
		jLabel.setText("00:00");
		timer.cancel();
		timer = new Timer();
		format = NumberFormat.getNumberInstance();
		format.setMinimumIntegerDigits(2);
		timer.scheduleAtFixedRate(new TimerTask() {  
			@Override
			public void run() {// neu dang choi
				second++;
				jLabel.setText(format.format(second/60)+ ":"+ format.format(second%60));
			}
		}, 1000, 1000);
		
		button.setText("Stop Game");
	}
	private static void stopGame() {
		button.setText("Start");
		second = 0;
		jLabel.setText("00:00");
		timer.cancel();
		timer = new Timer();
		board.reset();
	}
	
}
		

		
		
	
	










