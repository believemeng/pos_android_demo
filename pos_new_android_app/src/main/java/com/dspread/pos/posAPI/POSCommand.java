package com.dspread.pos.posAPI;

import com.dspread.xpos.QPOSService;

import java.util.Hashtable;

public class POSCommand {
    public QPOSService pos;
    public static POSCommand posCommand;
    public static POSCommand getInstance(){
        if(posCommand == null){
            synchronized (POSCommand.class) {
                if (posCommand == null) {
                    posCommand = new POSCommand();
                }
            }
        }
        return posCommand;
    }

    public void setQPOSService(QPOSService pos){
        this.pos = pos;
    }
    public void setCardTradeMode(QPOSService.CardTradeMode mode){
        pos.setCardTradeMode(mode);
    }
    public void doTrade(int timeout){
        pos.doTrade(timeout);
    }
    
    public void setAmount(String amount, String cashbackAmounts, String currencyCode, QPOSService.TransactionType transactionType){
        pos.setAmount(amount, cashbackAmounts, currencyCode, transactionType);
    }
    
    public void sendTime(String terminalTime){
        pos.sendTime(terminalTime);
    }
    
    public void selectEmvApp(int position){
        pos.selectEmvApp(position);
    }
    
    public void cancelSelectEmvApp(){
        pos.cancelSelectEmvApp();
    }
    
    public void pinMapSync(String value, int timeout){
        pos.pinMapSync(value, timeout);
    }
    
    public void cancelPin(){
        pos.cancelPin();
    }

    public boolean isOnlinePin(){
        return pos.isOnlinePin();
    }

    public int getCvmPinTryLimit(){
        return pos.getCvmPinTryLimit();
    }
    
    public void bypassPin(){
        pos.sendPin("".getBytes());
    }
    
    public void sendCvmPin(String pinBlock, boolean isEncrypted){
        pos.sendCvmPin(pinBlock, isEncrypted);
    }

    public Hashtable<String,String> getEncryptData(){
        return pos.getEncryptData();
    }

    public Hashtable<String, String> getNFCBatchData(){
        return pos.getNFCBatchData();
    }

    public void doEmvApp(QPOSService.EmvOption option){
        pos.doEmvApp(option);
    }
    
    public void sendOnlineProcessResult(String tlv){
        pos.sendOnlineProcessResult(tlv);
    }
    
    public void cancelTrade(){
        pos.cancelTrade();
    }

    public Hashtable<String, String> anlysEmvIccData(String tlv){
        return pos.anlysEmvIccData(tlv);
    }
}
