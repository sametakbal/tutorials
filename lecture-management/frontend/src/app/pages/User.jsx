import { Grid, Icon, Label, Menu, Table } from "semantic-ui-react";

export default function User() {
  return (
    <>
      <Grid>
        <Grid.Column width={10}>
          <Table celled>
            <Table.Header>
              <Table.Row>
                <Table.HeaderCell>Header</Table.HeaderCell>
                <Table.HeaderCell>Header</Table.HeaderCell>
                <Table.HeaderCell>Header</Table.HeaderCell>
              </Table.Row>
            </Table.Header>

            <Table.Body>
              <Table.Row>
                <Table.Cell>First</Table.Cell>
                <Table.Cell>Cell</Table.Cell>
                <Table.Cell>Cell</Table.Cell>
              </Table.Row>
              <Table.Row>
                <Table.Cell>Cell</Table.Cell>
                <Table.Cell>Cell</Table.Cell>
                <Table.Cell>Cell</Table.Cell>
              </Table.Row>
              <Table.Row>
                <Table.Cell>Cell</Table.Cell>
                <Table.Cell>Cell</Table.Cell>
                <Table.Cell>Cell</Table.Cell>
              </Table.Row>
            </Table.Body>

            <Table.Footer>
              <Table.Row>
                <Table.HeaderCell colSpan="3">
                  <Menu floated="right" pagination>
                    <Menu.Item as="a" icon>
                      <Icon name="chevron left" />
                    </Menu.Item>
                    <Menu.Item as="a">1</Menu.Item>
                    <Menu.Item as="a">2</Menu.Item>
                    <Menu.Item as="a">3</Menu.Item>
                    <Menu.Item as="a">4</Menu.Item>
                    <Menu.Item as="a" icon>
                      <Icon name="chevron right" />
                    </Menu.Item>
                  </Menu>
                </Table.HeaderCell>
              </Table.Row>
            </Table.Footer>
          </Table>
        </Grid.Column>
        <Grid.Column width={4}>
          <div className="ui form">
            <div className="field">
              <label>Name</label>
              <input type="text" name="name" placeholder="First Name" />
            </div>
            <div className="field">
              <label>Surname</label>
              <input type="text" name="surname" placeholder="Surname" />
            </div>
            <div className="field">
              <label>Identity No</label>
              <input type="text" name="identityNo" placeholder="Identity No" />
            </div>
            <div className="ui form">
              <div className="field">
                <label>Gender</label>
                <select>
                  <option value="">Gender</option>
                  <option value="MALE">Male</option>
                  <option value="FEMALE">Female</option>
                </select>
              </div>
            </div>
            <div className="ui form">
              <div className="field">
                <label>Role</label>
                <select>
                  <option className="item" value="">
                    Role
                  </option>
                  <option className="item" value="STUDENT">
                    Student
                  </option>
                  <option className="item" value="TEACHER">
                    Teacher
                  </option>
                </select>
              </div>
            </div>

            <div className="ui primary submit button">Submit</div>
            <div className="ui submit button">Clear Form</div>
          </div>
        </Grid.Column>
      </Grid>
    </>
  );
}
