package agenda;


/**
* agenda/AgendasHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Agenda.idl
* Terça-feira, 14 de Outubro de 2014 09h18min34s BRT
*/

public final class AgendasHolder implements org.omg.CORBA.portable.Streamable
{
  public String value[] = null;

  public AgendasHolder ()
  {
  }

  public AgendasHolder (String[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = agenda.AgendasHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    agenda.AgendasHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return agenda.AgendasHelper.type ();
  }

}
