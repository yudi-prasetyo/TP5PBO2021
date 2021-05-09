/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulgame;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Fauzan
 */
public class dbConnection {
    public static Connection con;
    public static Statement stm;
    
    public void connect(){//untuk membuka koneksi ke database
        try {
            String url ="jdbc:mysql://localhost/db_gamepbo";
            String user="root";
            String pass="";
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url,user,pass);
            stm = con.createStatement();
            System.out.println("koneksi berhasil;");
        } catch (Exception e) {
            System.err.println("koneksi gagal" +e.getMessage());
        }
    }
    
    public DefaultTableModel readTable(){
        
        DefaultTableModel dataTabel = null;
        try{
            Object[] column = {"No", "Username", "Total Score", "Score Game", "Duration"};
            connect();
            dataTabel = new DefaultTableModel(null, column);
            String sql = "Select * from highscore ORDER BY total_score DESC";
            ResultSet res = stm.executeQuery(sql);
            
            int no = 1;
            while(res.next()){
                Object[] hasil = new Object[5];
                hasil[0] = no;
                hasil[1] = res.getString("Username");
                hasil[2] = res.getString("total_score");
                hasil[3] = res.getString("score");
                hasil[4] = res.getString("duration");
                no++;
                dataTabel.addRow(hasil);
            }
        }catch(Exception e){
            System.err.println("Read gagal " +e.getMessage());
        }
        
        return dataTabel;
    }
    
    public void saveData(Player player) {
        connect();
        
        if (isExists(player)) {
            String sql = String.format("UPDATE highscore "
                    + "SET total_score = %d, score = %d, duration = %d "
                    + "WHERE username = '%s' AND total_score < %d",
                    player.getTotalScore(), player.getScore(), player.getDuration(), player.getUsername(), player.getTotalScore());

            try {
                stm.executeUpdate(sql);
            } catch (SQLException ex) {
                Logger.getLogger(dbConnection.class.getName()).log(Level.SEVERE, null, ex);
            }    
        } else {
            String sql = String.format("INSERT INTO highscore (Username, total_score, score, duration) VALUES('%s', %d, %d, %d)", 
                    player.getUsername(), player.getTotalScore(), player.getScore(), player.getDuration());

            try {
                stm.executeUpdate(sql);
            } catch (SQLException ex) {
                Logger.getLogger(dbConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    // To check if the username is already available and the score is smaller than the new one
    public boolean isExists(Player player) {
        connect();
        String sql = String.format("SELECT Username FROM highscore WHERE Username = '%s'", 
                player.getUsername());
        
        try {
            ResultSet rs = stm.executeQuery(sql);
            if (rs.next())
                return true;
        } catch (SQLException ex) {
            Logger.getLogger(dbConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
}
