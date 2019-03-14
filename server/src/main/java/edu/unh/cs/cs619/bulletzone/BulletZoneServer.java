/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.unh.cs.cs619.bulletzone;

import org.springframework.boot.autoconfigure.*;

import edu.unh.cs.cs619.bulletzone.datalayer.BulletZoneData;


@SpringBootApplication
public class BulletZoneServer {
    public static void main(String[] args) {
        String url = "jdbc:mysql://stman1.cs.unh.edu:3306/cs6195";
        String username = "mdp";
        String password = "Drag56kes";

        BulletZoneData d = new BulletZoneData(url, username, password);
        //d.rebuildData();
        //d.listTables();
        d.listWeaponTypes();
        d.close();

        //SpringApplication.run(BulletZoneServer.class, args);
    }
}