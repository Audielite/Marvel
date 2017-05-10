package com.company;

import org.asynchttpclient.*;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created by alexishennings on 5/7/17.
 */
public class MarvelGUI extends JFrame {
    private JPanel rootPanel;
    private JList resultsList;
    private JButton searchButtons;
    private JTextField searchField;

    private DefaultListModel searchResultsModel;

    //protected Comics superHero;

    protected MarvelGUI() throws Exception {
        setContentPane(rootPanel);
        pack();
        setPreferredSize(new Dimension(950, 950));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        searchResultsModel = new DefaultListModel();
        resultsList.setModel(searchResultsModel);
        configureButtons();
    }

    protected void configureButtons() throws Exception {
        BufferedReader readers = new BufferedReader(new FileReader("keys.txt"));

        final String publicKey = readers.readLine();
        final String privateKey = readers.readLine();

        //@Override
        searchButtons.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final String main = searchField.getText();


                Random rnd = new Random();
                int ts = rnd.nextInt(10000000) + 10000000;
                System.out.println(ts);

                String hashMe = ts + privateKey + publicKey;

                MessageDigest md = null;
                try {
                    md = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException e1) {
                    e1.printStackTrace();
                }
                byte[] keyBytes = hashMe.getBytes();
                byte[] hashBytes = md.digest(keyBytes);

                String hash = String.format("%032X", new BigInteger(1, hashBytes)); //http://stackoverflow.com/questions/5470219/get-md5-string-from-message-digest
                System.out.println(hash.toLowerCase());
                hash = hash.toLowerCase();

                // Now have ts and hash, and can make API call.

                String baseurl = "https://gateway.marvel.com:443/v1/public/characters?name=" + main + "&ts=%s&apikey=%s&hash=%s";
                //String linkyLink = "https://gateway.marvel.com:443/v1/public/characters/"+ books +"/comics?apikey=d292f75d9bcc3111477d639ee77ed1cb";
                String url = String.format(baseurl, ts, publicKey, hash);
                //String linkUrl = String.format(linkyLink, ts, publicKey, hash);

                System.out.println(url);
                //System.out.println(linkUrl);

                AsyncHttpClient c = new DefaultAsyncHttpClient();
                c.prepareGet(url).execute(new AsyncCompletionHandler<String>() {

                    @Override
                    public String onCompleted(Response response) throws Exception {

                        String responseBody = response.getResponseBody();
                        System.out.println(responseBody);
                        try {
                            JSONObject jsonResponse = new JSONObject(response.getResponseBody());
                            JSONArray resultArray = jsonResponse.getJSONObject("data").getJSONArray("results");

                            for (int x = 0; x < resultArray.length(); x++) {

                                JSONObject character = resultArray.getJSONObject(x);
                                String superHeroes = character.getString("name");
                                String bio = character.getString("description");
                                int books = character.getInt("id");

                                JSONObject thumb = character.getJSONObject("thumbnail");
                                String imageLink = thumb.getString("path");
                                String image = thumb.getString("extension");
                                //String image = null;
                                imageLink = String.format(imageLink + "/portrait_incredible." + image);

                                System.out.println(character.getString("name"));
                                System.out.println(character.getString("description"));

                                //Main newMain = new Main(main);
                                //searchResultsModel.addElement(newMain);
                                searchResultsModel.addElement(superHeroes + "'s Bio:");
                                searchResultsModel.addElement("");
                                searchResultsModel.addElement(bio);
                                searchResultsModel.addElement("Character ID is: " + books);
                                searchResultsModel.addElement(imageLink);
                                searchResultsModel.addElement("------------------");

                            }
                            return null;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;

                    }
                });
            }
        });
    }

}


        /*resultsList.addListSelectionListener(new ListSelectionListener() {
@Override
public void valueChanged(ListSelectionEvent e) {
        String tickets = imageArea.getText();
        JSONObject charThumb = character.getJSONObject("thumbnail");
        String imageURL = charThumb.getString("path");
        String imageExtension = charThumb.getString("extension");
        String fullImageURL;
        fullImageURL = String.format(imageURL + "/portrait_xlarge." + imageExtension);

        }
        });*/

            /*private void addKeyListener(ListSelectionListener listSelectionListener) {
                String charact = imageArea.getText();
                JSONObject charThumb = character.getJSONObject("thumbnail");
                String imageURL = charThumb.getString("path");
                String imageExtension = charThumb.getString("extension");
                String fullImageURL;
                fullImageURL = String.format(imageURL + "/portrait_xlarge." + imageExtension);
                addKeyListener(this);

            }
        });*/


/*final JList list = new JList();
    MouseListener mouseListener = new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {

                String selectedItem = (String) list.getSelectedValue();
                // add selectedItem to your second list.
                DefaultListModel model = (DefaultListModel) resultsList.getModel();
                if (model == null) {
                    model = new DefaultListModel();
                    resultsList.setModel(model);
                }
                model.addElement(selectedItem);

            }
        }
    };

    list.addMouseListener(mouseListener);*/