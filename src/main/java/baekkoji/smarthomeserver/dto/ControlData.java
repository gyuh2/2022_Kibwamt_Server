package baekkoji.smarthomeserver.dto;

import lombok.Data;

@Data
public class ControlData {

    private int angle=0;
    private int AC_temp=0;
    private int heater_temp=0;
    private boolean window = false;
    private boolean heater = false;
    private boolean AC = false;
    private boolean airCleaner = false;
    private boolean airOut = false;

    public int controlHome(){
        int result = 0;
        //DB에 저장하고, D 아두이노에 제어 요청을 하고 정상처리 되면 응답을 받아야함.

        result = 1;
        return result;
    }
}
