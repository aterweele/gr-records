(ns gr-records.data.sort)

(defn by-gender-last-name
  [ms]
  (sort
   (fn [{left-gender :gender left-name :last-name}
        {right-gender :gender right-name :last-name}]
     (let [gender-comapare (compare left-gender right-gender)]
       (if-not (zero? gender-comapare)
         gender-comapare
         (compare left-name right-name))))
   ms))

(defn by-birth-date
  [ms]
  (sort
   (fn [{left-date :birth-date} {right-date :birth-date}]
     (* -1 (.compareTo left-date right-date)))
   ms))
