package agenda;

/**
* agenda/AgendaHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Agenda.idl
* Terça-feira, 14 de Outubro de 2014 09h18min34s BRT
*/

public final class AgendaHolder implements org.omg.CORBA.portable.Streamable
{
  public agenda.Agenda value = null;

  public AgendaHolder ()
  {
  }

  public AgendaHolder (agenda.Agenda initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = agenda.AgendaHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    agenda.AgendaHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return agenda.AgendaHelper.type ();
  }

}
