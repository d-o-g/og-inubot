package com.inubot.bot.farm.irc;

import com.inubot.api.methods.Game;
import com.inubot.api.methods.Players;
import com.inubot.api.methods.Game;
import com.inubot.api.methods.Players;
import com.inubot.api.util.Time;

/**
 * Created by luckruns0ut on 02/05/15.
 */
public class IRCThread implements Runnable {
    @Override
    public void run() {
        try {
            while (true) {
                if(!IRC.isConnected()) {
                    System.out.println("Disconnected from server.");
                    if(IRC.connect()) {
                        System.out.println("Connected to server.");

                        IRC.sendLine("USER " + IRC.getNick() + " localhost " + IRC.getHost() + " " + IRC.getNick());
                        IRC.sendLine("NICK " + IRC.getNick());
                        IRC.sendLine("JOIN #rd-controlpanel");

                        try {
                            while (true) {
                                String line = IRC.readLine();
                                if(line != null) {
                                    IRCParser.parse(line);
                                }

                                try {
                                    if(IRC.isInitialized() && Game.isLoggedIn() && IRC.getNick().startsWith("RDBot")) {
                                        IRC.changeNick(Players.getLocal().getName());
                                        Time.sleep(2000);
                                    }
                                }catch (Exception ex){
                                    //ex.printStackTrace();
                                }

                                Time.sleep(1);
                            }
                        }catch (Exception ex ) {
                            ex.printStackTrace();
                        }
                    }
                }
                Time.sleep(1000);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
