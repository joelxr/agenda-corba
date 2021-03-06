package agenda;


/**
* agenda/AgendaOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Agenda.idl
* Terça-feira, 14 de Outubro de 2014 09h18min34s BRT
*/

public interface AgendaOperations 
{
  void addContact (agenda.Contact contact) throws agenda.ContactAlreadyExists;
  void updateContact (agenda.Contact contact);
  void deleteContact (agenda.Contact contact);
  agenda.Contact[] getContacts ();
  String getName ();
  void start ();
  void syncContact (agenda.Contact contact, String[] agendas, String action);
  void addNearbyAgenda (String name);
} // interface AgendaOperations
