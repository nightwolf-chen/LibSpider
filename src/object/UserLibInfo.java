/*
 * Copyright 2014 bruce.
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
package object;

import db.DBPersitance;
import java.util.List;
import object.BookBorrowHistory;
import object.User;

/**
 *
 * @author bruce
 */
public class UserLibInfo implements DBPersitance {

    private final User user;
    private final List<Book> borrowList;

//    public UserLibInfo(User user, List<Object> borrowList) {
//        this.user = user;
//        this.borrowList = borrowList;
//    }

    public UserLibInfo(User user, List<Book> borrowList) {
        this.user = user;
        this.borrowList = borrowList;
    }

    public User getUser() {
        return user;
    }

    public List<Book> getBorrowList() {
        return borrowList;
    }

    @Override
    public void saveToDB() {

        this.user.saveToDB();

        for (Object obj : borrowList) {
            BookBorrowHistory borrowHistory = (BookBorrowHistory) obj;
            borrowHistory.saveToDB();
        }

        System.out.println("Singler User Result finished saving...");
    }

    @Override
    public boolean exists() {
        return this.user.exists();
    }
}
