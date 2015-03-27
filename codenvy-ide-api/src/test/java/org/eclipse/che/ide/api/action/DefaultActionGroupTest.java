/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.api.action;

import org.eclipse.che.ide.api.constraints.Constraints;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.eclipse.che.ide.api.constraints.Anchor.AFTER;
import static org.eclipse.che.ide.api.constraints.Anchor.BEFORE;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mihail Kuznyetsov.
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultActionGroupTest {

    @Mock
    Action firstAction;

    @Mock
    Action secondAction;

    @Mock
    Action thirdAction;

    @Mock
    Action fourthAction;

    @Mock
    Action fifthAction;

    @Mock
    Action sixthAction;

    @Mock
    ActionManager actionManager;

    DefaultActionGroup defaultActionGroup;

    @Before
    public void setup() {
        defaultActionGroup = new DefaultActionGroup(actionManager);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAddSameActionTwice() {
        Action action = mock(Action.class);

        defaultActionGroup.add(action, new Constraints(AFTER, "someAction"));
        defaultActionGroup.add(action, new Constraints(BEFORE, "someAction"));
    }

    @Test
    public void shouldReturnEmptyArrayWhenThereIsNoActions() {
        // when
        Action[] result = defaultActionGroup.getChildren(mock(ActionEvent.class));

        //then
        assertThat(Arrays.asList(result)).isEmpty();
    }

    @Test
    public void addActionsWithNoExplicitConstraints() {
        // given
        mockRegisterActions();

        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction);
        defaultActionGroup.add(thirdAction);
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);

        // when
        Action[] result = defaultActionGroup.getChildren(mock(ActionEvent.class));

        //then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(
                firstAction,
                secondAction,
                thirdAction,
                fourthAction,
                fifthAction,
                sixthAction
        );
    }

    @Test
    public void addOneFirst() {
        // given
        mockRegisterActions();

        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction);
        defaultActionGroup.add(thirdAction, Constraints.FIRST);
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);

        // when
        Action[] result = defaultActionGroup.getChildren(mock(ActionEvent.class));

        //then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(
                thirdAction,
                firstAction,
                secondAction,
                fourthAction,
                fifthAction,
                sixthAction
        );
    }

    @Test
    public void addTwoFirst() {
        // given
        mockRegisterActions();

        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction);
        defaultActionGroup.add(thirdAction, Constraints.FIRST);
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction, Constraints.FIRST);
        defaultActionGroup.add(sixthAction);

        // when
        Action[] result = defaultActionGroup.getChildren(mock(ActionEvent.class));

        //then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(
                fifthAction,
                thirdAction,
                firstAction,
                secondAction,
                fourthAction,
                sixthAction
        );
    }

    @Test
    public void addOneLast() {
        // given
        mockRegisterActions();

        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction);
        defaultActionGroup.add(thirdAction, Constraints.LAST);
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);

        // when
        Action[] result = defaultActionGroup.getChildren(mock(ActionEvent.class));

        //then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(
                firstAction,
                secondAction,
                thirdAction,
                fourthAction,
                fifthAction,
                sixthAction
        );
    }

    @Test
    public void addOneBefore() {
        // given
        mockRegisterActions();

        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction);
        defaultActionGroup.add(thirdAction, new Constraints(BEFORE, "secondAction"));
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);


        // when
        Action[] result = defaultActionGroup.getChildren(mock(ActionEvent.class));

        //then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(
                firstAction,
                thirdAction,
                secondAction,
                fourthAction,
                fifthAction,
                sixthAction
        );
    }

    @Test
    public void addTwoBefore() {
        // given
        mockRegisterActions();

        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction);
        defaultActionGroup.add(thirdAction, new Constraints(BEFORE, "secondAction"));
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction, new Constraints(BEFORE, "secondAction"));
        defaultActionGroup.add(sixthAction);


        // when
        Action[] result = defaultActionGroup.getChildren(mock(ActionEvent.class));

        //then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(
                firstAction,
                thirdAction,
                fifthAction,
                secondAction,
                fourthAction,
                sixthAction
        );
    }

    @Test
    public void addOneBeforeNotAdded() {
        mockRegisterActions();
        // given
        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction);
        defaultActionGroup.add(thirdAction, new Constraints(BEFORE, "fifthAction"));
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);

        // when
        Action[] result = defaultActionGroup.getChildren(mock(ActionEvent.class));
        // then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(
                firstAction,
                secondAction,
                fourthAction,
                thirdAction,
                fifthAction,
                sixthAction
        );
    }

    @Test
    public void addComplexBefore() {
        mockRegisterActions();
        // given
        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction, new Constraints(BEFORE, "fourthAction"));
        defaultActionGroup.add(thirdAction);
        defaultActionGroup.add(fourthAction, new Constraints(BEFORE, "sixthAction"));
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);

        // when
        Action[] result = defaultActionGroup.getChildren(mock(ActionEvent.class));
        // then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(
                firstAction,
                thirdAction,
                fifthAction,
                secondAction,
                fourthAction,
                sixthAction
        );
    }

    @Test
    public void addOneAfter() {
        // given
        mockRegisterActions();

        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction);
        defaultActionGroup.add(thirdAction);
        defaultActionGroup.add(fourthAction, new Constraints(AFTER, "firstAction"));
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);

        // when
        Action[] result = defaultActionGroup.getChildren(mock(ActionEvent.class));

        //then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(
                firstAction,
                fourthAction,
                secondAction,
                thirdAction,
                fifthAction,
                sixthAction
        );
    }

    @Test
    public void addTwoAfter() {
        // given
        mockRegisterActions();

        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction, new Constraints(AFTER, "fifthAction"));
        defaultActionGroup.add(thirdAction, new Constraints(AFTER, "fifthAction"));
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);


        // when
        Action[] result = defaultActionGroup.getChildren(mock(ActionEvent.class));

        //then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(
                firstAction,
                fourthAction,
                fifthAction,
                thirdAction,
                secondAction,
                sixthAction
        );
    }


    @Test
    public void addComplexAfter() {
        mockRegisterActions();
        // given
        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction, new Constraints(AFTER, "fifthAction"));
        defaultActionGroup.add(thirdAction);
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction, new Constraints(AFTER, "firstAction"));
        defaultActionGroup.add(sixthAction);

        // when
        Action[] result = defaultActionGroup.getChildren(mock(ActionEvent.class));
        // then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(
                firstAction,
                fifthAction,
                secondAction,
                thirdAction,
                fourthAction,
                sixthAction
        );
    }

    @Test
    public void addOneAfterNotAdded() {
        mockRegisterActions();
        // given
        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction, new Constraints(AFTER, "fifthAction"));
        defaultActionGroup.add(thirdAction);
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);

        // when
        Action[] result = defaultActionGroup.getChildren(mock(ActionEvent.class));
        // then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(
                firstAction,
                thirdAction,
                fourthAction,
                fifthAction,
                secondAction,
                sixthAction
        );
    }

    @Test
    public void addActionToTheEndWhenConstraintUnsatisfied() {
        mockRegisterActions();
        // given
        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction, new Constraints(AFTER, "tenthAction"));
        defaultActionGroup.add(thirdAction);
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);

        // when
        Action[] result = defaultActionGroup.getChildren(mock(ActionEvent.class));
        // then
        assertThat(Arrays.asList(result)).hasSize(6).containsExactly(
                firstAction,
                thirdAction,
                fourthAction,
                fifthAction,
                sixthAction,
                secondAction
        );
    }

    @Test
    public void shouldResortAllActionsAfterAddingOne() {
        // add some actions
        defaultActionGroup.add(firstAction);
        when(actionManager.getId(eq(firstAction))).thenReturn("firstAction");

        defaultActionGroup.add(secondAction, Constraints.FIRST);
        when(actionManager.getId(eq(secondAction))).thenReturn("secondAction");

        defaultActionGroup.add(thirdAction, new Constraints(AFTER, "fourthAction"));
        when(actionManager.getId(eq(thirdAction))).thenReturn("thirdAction");

        // verify order
        Action[] result = defaultActionGroup.getChildren(mock(ActionEvent.class));

        assertThat(Arrays.asList(result)).containsExactly(
                secondAction,
                firstAction,
                thirdAction
        );

        // add other actions
        defaultActionGroup.add(fourthAction);
        when(actionManager.getId(eq(thirdAction))).thenReturn("thirdAction");

        defaultActionGroup.add(fifthAction, Constraints.FIRST);
        when(actionManager.getId(eq(fifthAction))).thenReturn("fifthAction");

        defaultActionGroup.add(sixthAction, new Constraints(BEFORE, "firstAction"));
        when(actionManager.getId(eq(sixthAction))).thenReturn("sixthAction");

        // verify that actions have been resorted
        Action[] newResult = defaultActionGroup.getChildren(mock(ActionEvent.class));

        assertThat(Arrays.asList(newResult)).hasSize(6).containsExactly(
                fifthAction,
                secondAction,
                sixthAction,
                firstAction,
                fourthAction,
                thirdAction
        );
    }

    @Test
    public void removeOneAction() {
        mockRegisterActions();

        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction);
        defaultActionGroup.add(thirdAction);
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);

        defaultActionGroup.remove(thirdAction);

        Action[] result = defaultActionGroup.getChildren(mock(ActionEvent.class));
        assertThat(Arrays.asList(result)).containsExactly(
                firstAction,
                secondAction,
                fourthAction,
                fifthAction,
                sixthAction
        );
    }

    @Test
    public void removeOneActionAndResortConstraints() {
        mockRegisterActions();

        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction);
        defaultActionGroup.add(thirdAction);
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);

        defaultActionGroup.remove(fourthAction);

        Action[] result = defaultActionGroup.getChildren(mock(ActionEvent.class));
        assertThat(Arrays.asList(result)).containsExactly(
                firstAction,
                secondAction,
                thirdAction,
                fifthAction,
                sixthAction
        );
    }

    @Test
    public void removeAllActions() {
        mockRegisterActions();

        defaultActionGroup.add(firstAction);
        defaultActionGroup.add(secondAction);
        defaultActionGroup.add(thirdAction);
        defaultActionGroup.add(fourthAction);
        defaultActionGroup.add(fifthAction);
        defaultActionGroup.add(sixthAction);

        defaultActionGroup.removeAll();

        Action[] result = defaultActionGroup.getChildren(mock(ActionEvent.class));
        assertThat(Arrays.asList(result)).isEmpty();

    }

    private void mockRegisterActions() {
        when(actionManager.getId(eq(firstAction))).thenReturn("firstAction");
        when(actionManager.getId(eq(secondAction))).thenReturn("secondAction");
        when(actionManager.getId(eq(thirdAction))).thenReturn("thirdAction");
        when(actionManager.getId(eq(fourthAction))).thenReturn("fourthAction");
        when(actionManager.getId(eq(fifthAction))).thenReturn("fifthAction");
        when(actionManager.getId(eq(sixthAction))).thenReturn("sixthAction");
    }
}
