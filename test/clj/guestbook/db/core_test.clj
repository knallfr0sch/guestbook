(ns guestbook.db.core-test
(:require [clojure.test :refer :all]
          [guestbook.config :refer [env]]
          [guestbook.db.core :refer [*db*] :as db]
          [java-time.pre-java8]
          [luminus-migrations.core :as migrations]
          [mount.core :as mount]
          [next.jdbc :as jdbc]))
(use-fixtures
  :once
  (fn [f]
    (mount/start
     #'guestbook.config/env
     #'guestbook.db.core/*db*)
    (migrations/migrate ["migrate"] (select-keys env [:database-url]))
    (f)))
(deftest test-messages
  (jdbc/with-transaction [t-conn *db* {:rollback-only true}]
    (is (= 1 (db/save-message!
              t-conn
              {:name "Bob"
               :message "Hello, World"}
              {:connection t-conn})))
    (is (= {:name "Bob"
            :message "Hello, World"}
           (-> (db/get-messages t-conn {})
               (first)
               (select-keys [:name :message]))))))