package ru.atom.chat;


import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;



import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static test.generated.tables.Messages.MESSAGES;
import static test.generated.tables.Users.USERS;

@Controller
@RequestMapping("chat")
public class ChatController {
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private Queue<String> messages = new ConcurrentLinkedQueue<>();
    private Map<String, String> usersOnline = new ConcurrentHashMap<>();
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");


    File file = new File("chathistory.txt");

    String userName = "root";
    String password = "233029";
    String url = "jdbc:mysql://localhost:3306/atom";

    public ChatController() throws IOException {

        try (Connection conn = DriverManager.getConnection(url)) {
            // ...
            DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
            Result<Record> result = create.select().from(MESSAGES).fetch();

            for(Record r : result) {
                Integer id = r.getValue(MESSAGES.ID);
                String username = r.getValue(MESSAGES.MSG_USERNAME);
                String usertext = r.getValue(MESSAGES.MSG_TEXT);
                String usertime = r.getValue(MESSAGES.MSG_TIME);

                messages.add("[" + usertime + "] " + username + ": " + usertext);

               // System.out.println(id + username + usertext + usertime);
            }

        }

        // For the sake of this tutorial, let's keep exception handling simple
        catch (Exception e) {
            e.printStackTrace();
            if(file.exists())
            {
                FileReader fr = new FileReader(file);
                BufferedReader reader = new BufferedReader(fr);
                String line = reader.readLine();
                while (line != null) {
                    messages.add(line);
                    line = reader.readLine();
                }
            }
        }

    }


    /**
     * curl -X POST -i localhost:8080/chat/login -d "name=I_AM_STUPID"
     */
    @RequestMapping(
            path = "login",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> login(@RequestParam("name") String name) {
        if (name.length() < 1) {
            return ResponseEntity.badRequest().body("Too short name, sorry :(");
        }
        if (name.length() > 200) {
            return ResponseEntity.badRequest().body("Too long name, sorry :(");
        }
        try (Connection conn = DriverManager.getConnection(url, userName, password)) {
            DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
            Result<Record> result = create.select().from(USERS).where(USERS.NAME.equal(name)).fetch();

            if(!result.isEmpty()){
                return ResponseEntity.badRequest().body("Already logged in:(");
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
       // if (usersOnline.containsKey(name)) {
        //    return ResponseEntity.badRequest().body("Already logged in:(");
       // }



        usersOnline.put(name, name);
        cal = Calendar.getInstance();
        String msg = "[" + sdf.format(cal.getTime()) + "] " + name + " logged in";
        messages.add(msg);

        try (Connection conn = DriverManager.getConnection(url, userName, password)) {
            // ...
            DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
            create.insertInto(MESSAGES, MESSAGES.MSG_USERNAME,MESSAGES.MSG_TEXT,MESSAGES.MSG_TIME)
                    .values(name,"Logged in",sdf.format(cal.getTime()))
                    .execute();
            create.insertInto(USERS, USERS.NAME)
                    .values(name)
                    .execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try(FileWriter writer = new FileWriter("chathistory.txt", true))
        {
            String text = msg;
            writer.append(text);
            writer.append('\n');
            writer.flush();
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    /**
     * curl -i localhost:8080/chat/chat
     */
    @RequestMapping(
            path = "chat",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> chat() {
        return new ResponseEntity<>(messages.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n")),
                HttpStatus.OK);
    }


    /**
     * curl -i localhost:8080/chat/online
     */
    @RequestMapping(
            path = "online",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity online() {
        String responseBody = String.join("\n", usersOnline.keySet().stream().sorted().collect(Collectors.toList()));
        return ResponseEntity.ok(responseBody);
    }
    /**
     * curl -X POST -i localhost:8080/chat/logout -d "name=I_AM_STUPID"
     */
    @RequestMapping(
            path = "logout",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> logout(@RequestParam("name") String name) {
        if (usersOnline.containsKey(name)) {
            usersOnline.remove(name);
            cal = Calendar.getInstance();
            String msg = "[" + sdf.format(cal.getTime()) + "] " + name + " logged out";
            messages.add(msg);

            try (Connection conn = DriverManager.getConnection(url, userName, password)) {
                // ...
                DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
                create.insertInto(MESSAGES, MESSAGES.MSG_USERNAME,MESSAGES.MSG_TEXT,MESSAGES.MSG_TIME)
                        .values(name,"Logged out",sdf.format(cal.getTime()))
                        .execute();
                create.delete(USERS)
                        .where(USERS.NAME.equal(name))
                        .execute();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            try(FileWriter writer = new FileWriter("chathistory.txt", true))
            {
                String text = msg;
                writer.append(text);
                writer.append('\n');
                writer.flush();
            }
            catch(IOException ex){

                System.out.println(ex.getMessage());
            }
            return ResponseEntity.ok("success");
        } else
            return ResponseEntity.badRequest().body(name + " is not found");

    }


    /**
     * curl -X POST -i localhost:8080/chat/say -d "name=I_AM_STUPID&msg=Hello everyone in this chat"
     */
    @RequestMapping(
            path = "say",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> say(@RequestParam("name") String name, @RequestParam("msg") String msg)  {
        if (usersOnline.containsKey(name)) {
            cal = Calendar.getInstance();

            String msgstring = "[" + sdf.format(cal.getTime()) + "] " + name + ": " + msg;
            messages.add(msgstring);


            try (Connection conn = DriverManager.getConnection(url, userName, password)) {
                // ...
                DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
                create.insertInto(MESSAGES, MESSAGES.MSG_USERNAME,MESSAGES.MSG_TEXT,MESSAGES.MSG_TIME)
                        .values(name,msg,sdf.format(cal.getTime()))
                        .execute();
            }

            // For the sake of this tutorial, let's keep exception handling simple
            catch (Exception e) {
                e.printStackTrace();
            }


            try(FileWriter writer = new FileWriter("chathistory.txt", true))
            {
                String text = msgstring;
                writer.append(text);
                writer.append('\n');
                writer.flush();
            }
            catch(IOException ex){

                System.out.println(ex.getMessage());
            }

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().body("User is not online");
    }
}
