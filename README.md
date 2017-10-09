
FredList
========

FredList is a simple Todo-List App for Android, primarily created for self-use. It is specialized on recurring items, like a shopping list, where you would usually need to buy a subset of the same things every time.

There can be several Lists and within those several Categories (the same for each List). Within a Category you add entries to a template, and then activate those Entries to appear on the actual ToDo-list. Each Entry can be in several Lists, but only in one Category.

This App is kept rather simple: No fancy synching, deadlines or other advanced features. Just ticking off entries from a recurring list. Done Entries are sorted to the bottom, as are Entries set to low priority, whereas Entries set to high priority are at the top.

Data Storage
---------------

The app data is stored as a JSON file in Internal Storage. You can also export to External Storage manually (and import from it of course). It also creates automatic backups to Internal Storage, just in case you have to revert to a previous state for some reason.

Data Import
--------------

Create a text file in the following format to import initial data. Current data is completely replaced by the import, so it's only suitable to get started, so you don't have to type in a huge list on your mobile device.

```
[FredList]
List 1
	Category 1
		Entry 1
		Entry 2
		Entry 3
		 Notes to Entry 3
		Entry 4
		 [List 2, List 3]
List 2
	Category 2
		Entry 5
		 Notes to Entry 5
	Category 1
		Entry 6
```

Indentations are one (Category) or two (Entry) TAB characters, and one additional space character for notes or adding the item to additional Lists.

Place the text file in external storage and import it from within the App. You can review what items it found and how they are categorized before the import is completed.

License
=======

```
Copyright 2017 tduva

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```

Using Material Design Icons (<https://material.io/icons/>), including some modified versions (see source file comments), licensed under [Apache License Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt).

