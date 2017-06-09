package quiettimev1.konamgil.com.quiettime.PhoneNumber;

/**
 * Created by konamgil on 2017-06-08.
 */

public class ContactsInfoObject {
    // 이름
    private String name;
    // 전화번호
    private String teleNumber;
    //체크
    private boolean checkbox;

    // 생성자
    public ContactsInfoObject(String name, String teleNumber, boolean status) {
        this.name = name;
        this.teleNumber = teleNumber;
        this.checkbox = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeleNumber() {
        return teleNumber;
    }

    public void setTeleNumber(String teleNumber) {
        this.teleNumber = teleNumber;
    }

    public boolean isCheckbox() {
        return checkbox;
    }

    public void setCheckbox(boolean checkbox) {
        this.checkbox = checkbox;
    }
}
