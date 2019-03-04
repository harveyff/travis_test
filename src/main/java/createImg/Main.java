package createImg;
import java.awt.Color;
import java.awt.Font;

public class Main {

    public static void main(String[] args) {
        String path = "D://testImg/";
        ImageUtils.pressText("2018.01.01",path + "ery.png",path + "eryy.png","宋体",Font.BOLD,Color.white,50, -138, -570, 1f);//测试OK
        ImageUtils.pressText("tiantian",path + "eryy.png",path + "/eryy.png","宋体",Font.BOLD,Color.white,50, 298, -570, 1f);//测试OK

        ImageUtils.pressImage(path + "erweima.png", path + "eryy.png",path + "eryy.png", -120, 500, 1f);//测试OK
        String text = "<p style='width:630px;height:216px;font-size:40px; line-height:56px;color:white;'>你爱我,我不爱你你爱我,我不爱你你爱我,我不爱你你爱我,我不爱你你爱我,我不爱你你爱我,我不爱你</p>";
        ImageUtils.drawStringCentered(text, "宋体", path + "text.png", 50, 1,  Color.white,815, 216);
        ImageUtils.pressImage(path + "text.png", path + "eryy.png",path + "eryy.png", 15, -340, 1f);//测试OK

    }

}
