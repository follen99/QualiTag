package it.unisannio.studenti.qualitag.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import it.unisannio.studenti.qualitag.model.Tag;
import it.unisannio.studenti.qualitag.repository.TagRepository;

@Route("")
public class AddTagView extends VerticalLayout {

  private final TagRepository repo;
  private VerticalLayout tag_list;

  public AddTagView(TagRepository repo) {
    this.repo = repo;
//    this.selectedTags = new HashSet<>();
    var title = new H1("Add a new tag");
    var value = new TextField();
    this.tag_list = new VerticalLayout();


    var saveButton = new Button("Save tag");
    saveButton.addClickListener(click -> {
      // Add the tag to the database
      var tag = this.repo.save(new Tag(value.getValue(), "test_project_id", "test_user_id"));
      this.updateTagList();
//      tag_list.add(createTagView(tag));

      // clearing the text field
      value.clear();
    });

    add(
      title,
      new HorizontalLayout(
          new HorizontalLayout(value, saveButton/*, deleteButton*/),
          new VerticalLayout(tag_list)

      )
    );

    this.updateTagList();
  }

  private void updateTagList() {
    var tags_from_db = this.repo.findAll();
    this.tag_list.removeAll();
    tags_from_db.forEach(tag -> {
      this.tag_list.add(createTagView(tag));
    });

    System.out.println(this.repo.findAll());
  }

  private Component createTagView(Tag tag) {
    Checkbox checkbox = new Checkbox(tag.getTagValue());
    return checkbox;
  }

}
