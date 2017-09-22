package com.amsu.healthy.bean;

public class ScoreInfo {
	private String iconUrl;
	private String username;
    private String province;
	private String sex;
	private int age;
	private int prematureCount;
	private int missCount;
	private int overScore;
	
	private int averageHeart;
	private int averageHeartScore;
	private int maxHeart;
	private int maxHeartScore;
	private int kcal;
	private int kcalScore;
	private int allscore;
	private int rank;


	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getPrematureCount() {
		return prematureCount;
	}

	public void setPrematureCount(int prematureCount) {
		this.prematureCount = prematureCount;
	}

	public int getMissCount() {
		return missCount;
	}

	public void setMissCount(int missCount) {
		this.missCount = missCount;
	}

	public int getOverScore() {
		return overScore;
	}

	public void setOverScore(int overScore) {
		this.overScore = overScore;
	}

	public int getAverageHeart() {
		return averageHeart;
	}

	public void setAverageHeart(int averageHeart) {
		this.averageHeart = averageHeart;
	}

	public int getAverageHeartScore() {
		return averageHeartScore;
	}

	public void setAverageHeartScore(int averageHeartScore) {
		this.averageHeartScore = averageHeartScore;
	}

	public int getMaxHeart() {
		return maxHeart;
	}

	public void setMaxHeart(int maxHeart) {
		this.maxHeart = maxHeart;
	}

	public int getMaxHeartScore() {
		return maxHeartScore;
	}

	public void setMaxHeartScore(int maxHeartScore) {
		this.maxHeartScore = maxHeartScore;
	}

	public int getKcal() {
		return kcal;
	}

	public void setKcal(int kcal) {
		this.kcal = kcal;
	}

	public int getKcalScore() {
		return kcalScore;
	}

	public void setKcalScore(int kcalScore) {
		this.kcalScore = kcalScore;
	}

	public int getAllscore() {
		return allscore;
	}

	public void setAllscore(int allscore) {
		this.allscore = allscore;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	@Override
	public String toString() {
		return "ScoreInfo{" +
				"iconUrl='" + iconUrl + '\'' +
				", username='" + username + '\'' +
				", province='" + province + '\'' +
				", sex='" + sex + '\'' +
				", age=" + age +
				", prematureCount=" + prematureCount +
				", missCount=" + missCount +
				", overScore=" + overScore +
				", averageHeart=" + averageHeart +
				", averageHeartScore=" + averageHeartScore +
				", maxHeart=" + maxHeart +
				", maxHeartScore=" + maxHeartScore +
				", kcal=" + kcal +
				", kcalScore=" + kcalScore +
				", allscore=" + allscore +
				", rank=" + rank +
				'}';
	}

	public ScoreInfo(String iconUrl, String username, String province, String sex, int age, int prematureCount, int missCount, int overScore, int averageHeart, int averageHeartScore, int maxHeart, int maxHeartScore, int kcal, int kcalScore, int allscore, int rank) {
		this.iconUrl = iconUrl;
		this.username = username;
		this.province = province;
		this.sex = sex;
		this.age = age;
		this.prematureCount = prematureCount;
		this.missCount = missCount;
		this.overScore = overScore;
		this.averageHeart = averageHeart;
		this.averageHeartScore = averageHeartScore;
		this.maxHeart = maxHeart;
		this.maxHeartScore = maxHeartScore;
		this.kcal = kcal;
		this.kcalScore = kcalScore;
		this.allscore = allscore;
		this.rank = rank;
	}
}
