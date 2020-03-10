package fr.upem.conc.lab2;

public class Foo {
  private String value;

  public Foo(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  private static Foo f;

  public static void main(String[] args) {

    new Thread(() ->
    {
      System.out.println(f.value);
    }).start();

    f = new Foo("1");
  }
}
