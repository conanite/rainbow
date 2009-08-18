(= object-types (table))

(mac create-object (name . props)
  (with (p (pair props)
         max-obj-id   (mksym "max-"  name "-id*"     )
         obj-db       (mksym ""      name "-db*"     )
         obj-dir      (mksym ""      name "-dir*"    )
         obj-from-req (mksym ""      name "-from-req")
         save-obj     (mksym "save-" name ""         )
         load-all     (mksym "load-" name "-db"      ))
    `(do (= ,max-obj-id           0
            ,obj-db               nil
            ,obj-dir              ,(string "arc/" name "/")
            (object-types ',name) ',p)
         (deftem ,name ,@(map car p))
         (def ,obj-from-req (req)
             (inst ',name 'id (++ ,max-obj-id)
                          ,@(mappend [list (list 'quote car._) `(arg req ,(string car._))] p)))
         (def ,save-obj (object)
           (save-table object (string ,obj-dir c!id))
           object)
         (def ,load-all ()
           (= ,obj-db (table))
           (each id (map int (dir ,obj-dir))
             (= ,max-obj-id (max ,max-obj-id id)
               (,obj-db id) (temload ',name (string ,obj-db id))))))))

(create-object contact 
  home-url "home url"  
  first    "first name"
  last     "last name" 
  emails   "emails"    
  phones   "phones"    
  urls     "urls"      
  addr     "address")

(= ab-app-thread    nil)

(def start-ab-app ((o port 8082))
  (prn "starting absv on port " port)
  (= ab-app-thread (thread (absv port))))

(def absv (port)
  (on-err (fn (ex) (details ex))
    (fn ()
        (ensure-dir contact-dir*)
        (prn "contact database established")
        (unless contacts* (load-contacts))
        (prn "contacts loaded: " len.contacts*)
        (asv port))))

(def as-table (obj-type object)
  (tag table
    (tag caption (pr obj-type #\# object!id))
    (each (k label) object-types.obj-type
      (row label object.k))))

(def create-form (obj-type)
  (tag table
    (tag caption (pr "new " obj-type))
    (each (k label) object-types.obj-type
      (row label (input type 'text name k)))))

(def tabular (obj-type objs)
  (tag table
    (tag tr
      (each prop object-types.obj-type
        (tag th (pr cadr.prop))))
    (each (id object) objs 
      (tag tr
        (each prop object-types.obj-type
          (tag td (pr (object car.prop))))))))

(def ablinks ()
  (tag (div style "width:100%;background:#EEF;color:#226;")
    (link "all contacts" "/contacts/list")
    (pr " - ")
    (link "new contact" "/contacts/new")))

(mac abpage (title . body)
  `(whitepage (center
    (ablinks)
    (pr ,title)
    ,@body)))

(defop contacts/new req
  (abpage "new contact"
    (tag (form action "/contacts/create" method "post")
      (create-object-form 'contact))))

(defopr contacts/create req
  (string "/contacts/show?id=" ((save-contact (contact-from-req req)) 'id)))

(defop contacts/show req
  (let id (coerce (arg req "id") 'int)
    (abpage (string "contact#" id)
      (as-table 'contact (contact-db* id)))))

(defop contacts/list req
  (abpage "all contacts")
    (tag table
      (tag tr
        (each prop contact-props
          (tag th (pr prop))))
      (each (id c) contact-db* (show-contact-row c))))
