package com.amsu.healthy.bean;

/**
 * Created by HP on 2016/11/30.
 */
public class User {
    private String phone;
    private String username;
    private String birthday;
    private String sex;
    private String weight;
    private String height;
    private String area;
    private String email;
    private String icon;

    public User() {
    }

    public User(String phone, String username, String birthday, String sex, String weight, String height, String area) {
        this.phone = phone;
        this.username = username;
        this.birthday = birthday;
        this.sex = sex;
        this.weight = weight;
        this.height = height;
        this.area = area;
    }

    public User(String phone, String username, String birthday, String sex, String weight, String height, String area, String email) {
        this.phone = phone;
        this.username = username;
        this.birthday = birthday;
        this.sex = sex;
        this.weight = weight;
        this.height = height;
        this.area = area;
        this.email = email;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public User(String phone, String username, String birthday, String sex, String weight, String height, String area, String email, String icon) {
        this.phone = phone;
        this.username = username;
        this.birthday = birthday;
        this.sex = sex;
        this.weight = weight;
        this.height = height;
        this.area = area;
        this.email = email;
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "User{" +
                "phone='" + phone + '\'' +
                ", username='" + username + '\'' +
                ", birthday='" + birthday + '\'' +
                ", sex='" + sex + '\'' +
                ", weight='" + weight + '\'' +
                ", height='" + height + '\'' +
                ", area='" + area + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
