package dependencies;

class Dep {
  private String name;
  private int age;
  private int address;
  
  public int getAge(){
    return age;
  }
  
  public void addYear(){
    int i = 1;
    age += i;
  }
  
  public String greet(String begin){
    return begin+", " + name + "!";
  }

}